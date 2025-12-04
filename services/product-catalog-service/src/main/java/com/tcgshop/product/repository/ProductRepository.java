package com.tcgshop.product.repository;

import com.tcgshop.product.model.Product;
import com.tcgshop.product.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Product entities
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products by category
     */
    List<Product> findByCategory(ProductCategory category);

    /**
     * Find products by name containing (case-insensitive)
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find products with stock quantity greater than specified value
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > :minStock")
    List<Product> findProductsInStock(@Param("minStock") Integer minStock);

    /**
     * Find product by ID with stock check
     */
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.stockQuantity > 0")
    Optional<Product> findByIdInStock(@Param("id") Long id);
}

