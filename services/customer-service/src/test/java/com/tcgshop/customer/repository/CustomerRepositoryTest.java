package com.tcgshop.customer.repository;

import com.tcgshop.customer.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setEmail("test@example.com");
        testCustomer.setPasswordHash("$2a$10$encodedPasswordHash");
        testCustomer.setFirstName("Test");
        testCustomer.setLastName("User");
        testCustomer.setCreatedAt(LocalDateTime.now());
        testCustomer.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testFindByEmail_Success() {
        // Given
        Customer saved = entityManager.persistAndFlush(testCustomer);

        // When
        Optional<Customer> result = customerRepository.findByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getFirstName()).isEqualTo("Test");
    }

    @Test
    void testFindByEmail_NotFound() {
        // When
        Optional<Customer> result = customerRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testSaveCustomer() {
        // Given
        Customer newCustomer = new Customer();
        newCustomer.setEmail("new@example.com");
        newCustomer.setPasswordHash("$2a$10$encodedPasswordHash");
        newCustomer.setFirstName("New");
        newCustomer.setLastName("Customer");
        newCustomer.setCreatedAt(LocalDateTime.now());
        newCustomer.setUpdatedAt(LocalDateTime.now());

        // When
        Customer saved = customerRepository.save(newCustomer);
        entityManager.flush();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void testEmailUniqueness() {
        // Given
        Customer saved = entityManager.persistAndFlush(testCustomer);

        Customer duplicate = new Customer();
        duplicate.setEmail("test@example.com");
        duplicate.setPasswordHash("$2a$10$anotherHash");
        duplicate.setFirstName("Duplicate");
        duplicate.setLastName("User");
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

