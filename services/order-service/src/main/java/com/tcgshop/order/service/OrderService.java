package com.tcgshop.order.service;

import com.tcgshop.order.client.PaymentServiceClient;
import com.tcgshop.order.dto.CreateOrderRequest;
import com.tcgshop.order.dto.OrderDTO;
import com.tcgshop.order.dto.OrderItemDTO;
import com.tcgshop.order.model.Order;
import com.tcgshop.order.model.OrderItem;
import com.tcgshop.order.model.OrderStatus;
import com.tcgshop.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                       PaymentServiceClient paymentServiceClient,
                       KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.paymentServiceClient = paymentServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderDTO createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setShippingAddressId(request.getShippingAddressId());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (var itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemRequest.getProductId());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setSubtotal(itemRequest.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            order.getItems().add(item);
            totalAmount = totalAmount.add(item.getSubtotal());
        }
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        // Publish Kafka event: order-created
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", savedOrder.getId());
        event.put("orderNumber", savedOrder.getOrderNumber());
        event.put("customerId", savedOrder.getCustomerId());
        event.put("totalAmount", savedOrder.getTotalAmount());
        kafkaTemplate.send("order-created", event);

        return convertToDTO(savedOrder);
    }

    public OrderDTO processPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Call Payment Service with Circuit Breaker
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", order.getId());
        paymentRequest.put("orderNumber", order.getOrderNumber());
        paymentRequest.put("amount", order.getTotalAmount());
        paymentRequest.put("customerId", order.getCustomerId());

        Map<String, Object> paymentResponse = paymentServiceClient.processPayment(paymentRequest);

        // Update order status based on payment response
        String paymentStatus = (String) paymentResponse.get("status");
        if ("COMPLETED".equals(paymentStatus)) {
            order.setStatus(OrderStatus.PAYMENT_COMPLETED);
        } else if ("PENDING".equals(paymentStatus)) {
            order.setStatus(OrderStatus.PAYMENT_PENDING);
        }

        orderRepository.save(order);

        // Publish Kafka event: order-status-updated
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", order.getId());
        event.put("orderNumber", order.getOrderNumber());
        event.put("status", order.getStatus().name());
        kafkaTemplate.send("order-status-updated", event);

        return convertToDTO(order);
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return convertToDTO(order);
    }

    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        order.setStatus(status);
        Order updated = orderRepository.save(order);

        // Publish Kafka event
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", updated.getId());
        event.put("orderNumber", updated.getOrderNumber());
        event.put("status", updated.getStatus().name());
        kafkaTemplate.send("order-status-updated", event);

        return convertToDTO(updated);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerId(order.getCustomerId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddressId(order.getShippingAddressId());
        dto.setItems(order.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList()));
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}

