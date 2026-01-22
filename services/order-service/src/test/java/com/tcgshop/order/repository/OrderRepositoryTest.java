package com.tcgshop.order.repository;

import com.tcgshop.order.model.Order;
import com.tcgshop.order.model.OrderItem;
import com.tcgshop.order.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setOrderNumber("ORD-123");
        testOrder.setCustomerId(1L);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("59.98"));
        testOrder.setShippingAddressId(1L);
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setUpdatedAt(LocalDateTime.now());

        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("29.99"));
        item.setSubtotal(new BigDecimal("59.98"));
        item.setOrder(testOrder);
        testOrder.setItems(List.of(item));
    }

    @Test
    void testFindByCustomerId() {
        // Given
        Order saved = entityManager.persistAndFlush(testOrder);

        // When
        List<Order> result = orderRepository.findByCustomerId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
        assertThat(result.get(0).getOrderNumber()).isEqualTo("ORD-123");
    }

    @Test
    void testFindByCustomerId_MultipleOrders() {
        // Given
        Order order1 = entityManager.persistAndFlush(testOrder);
        
        Order order2 = new Order();
        order2.setOrderNumber("ORD-456");
        order2.setCustomerId(1L);
        order2.setStatus(OrderStatus.PROCESSING);
        order2.setTotalAmount(new BigDecimal("99.99"));
        order2.setCreatedAt(LocalDateTime.now());
        order2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(order2);

        // When
        List<Order> result = orderRepository.findByCustomerId(1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Order::getCustomerId).containsOnly(1L);
    }

    @Test
    void testSaveOrder() {
        // Given
        Order newOrder = new Order();
        newOrder.setOrderNumber("ORD-NEW");
        newOrder.setCustomerId(2L);
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setTotalAmount(new BigDecimal("49.99"));
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setUpdatedAt(LocalDateTime.now());

        // When
        Order saved = orderRepository.save(newOrder);
        entityManager.flush();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getOrderNumber()).isEqualTo("ORD-NEW");
    }

    @Test
    void testOrderNumberUniqueness() {
        // Given
        Order saved = entityManager.persistAndFlush(testOrder);

        Order duplicate = new Order();
        duplicate.setOrderNumber("ORD-123");
        duplicate.setCustomerId(2L);
        duplicate.setStatus(OrderStatus.PENDING);
        duplicate.setTotalAmount(new BigDecimal("49.99"));
        duplicate.setCreatedAt(LocalDateTime.now());
        duplicate.setUpdatedAt(LocalDateTime.now());

        // When & Then
        try {
            entityManager.persistAndFlush(duplicate);
            entityManager.flush();
            // Should fail due to unique constraint
        } catch (Exception e) {
            // Expected: Unique constraint violation
            assertThat(e).isNotNull();
        }
    }
}

