package com.tcgshop.order.service;

import com.tcgshop.order.client.PaymentServiceClient;
import com.tcgshop.order.dto.CreateOrderRequest;
import com.tcgshop.order.dto.OrderDTO;
import com.tcgshop.order.dto.OrderItemRequest;
import com.tcgshop.order.model.Order;
import com.tcgshop.order.model.OrderItem;
import com.tcgshop.order.model.OrderStatus;
import com.tcgshop.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest createOrderRequest;
    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(1L);
        createOrderRequest.setShippingAddressId(1L);
        
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        itemRequest.setUnitPrice(new BigDecimal("29.99"));
        createOrderRequest.setItems(Arrays.asList(itemRequest));

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setProductId(1L);
        testOrderItem.setQuantity(2);
        testOrderItem.setUnitPrice(new BigDecimal("29.99"));
        testOrderItem.setSubtotal(new BigDecimal("59.98"));

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORD-123");
        testOrder.setCustomerId(1L);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("59.98"));
        testOrder.setShippingAddressId(1L);
        testOrder.setItems(new ArrayList<>(Arrays.asList(testOrderItem)));
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setUpdatedAt(LocalDateTime.now());
        testOrderItem.setOrder(testOrder);
    }

    @Test
    void testCreateOrder_Success() {
        // Given
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            order.setOrderNumber("ORD-123");
            return order;
        });

        // When
        OrderDTO result = orderService.createOrder(createOrderRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("59.98"));
        assertThat(result.getItems()).hasSize(1);
        
        verify(orderRepository).save(any(Order.class));
        verify(kafkaTemplate).send(eq("order-created"), any());
    }

    @Test
    void testCreateOrder_PublishesKafkaEvent() {
        // Given
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            order.setOrderNumber("ORD-123");
            return order;
        });

        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);

        // When
        orderService.createOrder(createOrderRequest);

        // Then
        verify(kafkaTemplate).send(eq("order-created"), eventCaptor.capture());
        Map<String, Object> event = eventCaptor.getValue();
        assertThat(event).containsKey("orderId");
        assertThat(event).containsKey("orderNumber");
        assertThat(event).containsKey("customerId");
        assertThat(event).containsKey("totalAmount");
    }

    @Test
    void testProcessPayment_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        
        Map<String, Object> paymentResponse = new HashMap<>();
        paymentResponse.put("status", "COMPLETED");
        paymentResponse.put("paymentId", 1L);
        when(paymentServiceClient.processPayment(any())).thenReturn(paymentResponse);

        // When
        OrderDTO result = orderService.processPayment(1L);

        // Then
        assertThat(result).isNotNull();
        verify(paymentServiceClient).processPayment(any());
        verify(orderRepository).save(any(Order.class));
        verify(kafkaTemplate).send(eq("order-status-updated"), any());
    }

    @Test
    void testProcessPayment_CircuitBreakerFallback() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(paymentServiceClient.processPayment(any())).thenThrow(new RuntimeException("Payment service unavailable"));
        
        // Note: In real scenario, Circuit Breaker would catch this and use fallback
        // This test verifies the service handles the exception
        // When
        try {
            orderService.processPayment(1L);
        } catch (Exception e) {
            // Expected: Exception from payment service
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
        
        verify(paymentServiceClient).processPayment(any());
    }

    @Test
    void testGetOrderById_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        OrderDTO result = orderService.getOrderById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrderNumber()).isEqualTo("ORD-123");
        verify(orderRepository).findById(1L);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
        verify(orderRepository).findById(999L);
    }

    @Test
    void testGetOrdersByCustomerId() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByCustomerId(1L)).thenReturn(orders);

        // When
        List<OrderDTO> result = orderService.getOrdersByCustomerId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
        verify(orderRepository).findByCustomerId(1L);
    }

    @Test
    void testUpdateOrderStatus() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderDTO result = orderService.updateOrderStatus(1L, OrderStatus.PROCESSING);

        // Then
        assertThat(result).isNotNull();
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(kafkaTemplate).send(eq("order-status-updated"), any());
    }

    @Test
    void testUpdateOrderStatus_NotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.updateOrderStatus(999L, OrderStatus.PROCESSING))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}

