package com.tcgshop.product.repository;

import com.tcgshop.product.model.Product;
import com.tcgshop.product.model.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        testProduct1 = new Product();
        testProduct1.setName("Deck Box Premium");
        testProduct1.setDescription("Premium deck box");
        testProduct1.setCategory(ProductCategory.DECK_BOX);
        testProduct1.setPrice(new BigDecimal("29.99"));
        testProduct1.setStockQuantity(100);
        testProduct1.setCreatedAt(LocalDateTime.now());
        testProduct1.setUpdatedAt(LocalDateTime.now());

        testProduct2 = new Product();
        testProduct2.setName("Card Holder");
        testProduct2.setDescription("Card holder");
        testProduct2.setCategory(ProductCategory.CARD_HOLDER);
        testProduct2.setPrice(new BigDecimal("19.99"));
        testProduct2.setStockQuantity(50);
        testProduct2.setCreatedAt(LocalDateTime.now());
        testProduct2.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testFindByCategory() {
        // Given
        Product saved1 = entityManager.persistAndFlush(testProduct1);
        Product saved2 = entityManager.persistAndFlush(testProduct2);

        // When
        List<Product> result = productRepository.findByCategory(ProductCategory.DECK_BOX);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(ProductCategory.DECK_BOX);
        assertThat(result.get(0).getName()).isEqualTo("Deck Box Premium");
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        // Given
        entityManager.persistAndFlush(testProduct1);
        entityManager.persistAndFlush(testProduct2);

        // When
        List<Product> result = productRepository.findByNameContainingIgnoreCase("deck");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).containsIgnoringCase("deck");
    }

    @Test
    void testFindProductsInStock() {
        // Given
        Product outOfStock = new Product();
        outOfStock.setName("Out of Stock");
        outOfStock.setCategory(ProductCategory.DECK_BOX);
        outOfStock.setPrice(new BigDecimal("29.99"));
        outOfStock.setStockQuantity(0);
        outOfStock.setCreatedAt(LocalDateTime.now());
        outOfStock.setUpdatedAt(LocalDateTime.now());

        entityManager.persistAndFlush(testProduct1);
        entityManager.persistAndFlush(outOfStock);

        // When
        List<Product> result = productRepository.findProductsInStock(0);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockQuantity()).isGreaterThan(0);
    }

    @Test
    void testFindByIdInStock() {
        // Given
        Product saved = entityManager.persistAndFlush(testProduct1);

        // When
        Optional<Product> result = productRepository.findByIdInStock(saved.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getStockQuantity()).isGreaterThan(0);
    }

    @Test
    void testFindByIdInStock_OutOfStock() {
        // Given
        Product outOfStock = new Product();
        outOfStock.setName("Out of Stock");
        outOfStock.setCategory(ProductCategory.DECK_BOX);
        outOfStock.setPrice(new BigDecimal("29.99"));
        outOfStock.setStockQuantity(0);
        outOfStock.setCreatedAt(LocalDateTime.now());
        outOfStock.setUpdatedAt(LocalDateTime.now());
        Product saved = entityManager.persistAndFlush(outOfStock);

        // When
        Optional<Product> result = productRepository.findByIdInStock(saved.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testSaveProduct() {
        // Given
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setCategory(ProductCategory.STORAGE_SOLUTION);
        newProduct.setPrice(new BigDecimal("49.99"));
        newProduct.setStockQuantity(25);
        newProduct.setCreatedAt(LocalDateTime.now());
        newProduct.setUpdatedAt(LocalDateTime.now());

        // When
        Product saved = productRepository.save(newProduct);
        entityManager.flush();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Product");
    }

    @Test
    void testDeleteProduct() {
        // Given
        Product saved = entityManager.persistAndFlush(testProduct1);
        Long id = saved.getId();

        // When
        productRepository.deleteById(id);
        entityManager.flush();

        // Then
        Optional<Product> result = productRepository.findById(id);
        assertThat(result).isEmpty();
    }
}

