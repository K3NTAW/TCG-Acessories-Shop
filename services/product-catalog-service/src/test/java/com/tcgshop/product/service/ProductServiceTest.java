package com.tcgshop.product.service;

import com.tcgshop.product.dto.CreateProductRequest;
import com.tcgshop.product.dto.ProductDTO;
import com.tcgshop.product.model.Product;
import com.tcgshop.product.model.ProductCategory;
import com.tcgshop.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private CreateProductRequest testRequest;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Deck Box");
        testProduct.setDescription("A test deck box");
        testProduct.setCategory(ProductCategory.DECK_BOX);
        testProduct.setPrice(new BigDecimal("29.99"));
        testProduct.setStockQuantity(100);
        testProduct.setImageUrl("https://example.com/image.jpg");
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        testRequest = new CreateProductRequest();
        testRequest.setName("Test Deck Box");
        testRequest.setDescription("A test deck box");
        testRequest.setCategory(ProductCategory.DECK_BOX);
        testRequest.setPrice(new BigDecimal("29.99"));
        testRequest.setStockQuantity(100);
        testRequest.setImageUrl("https://example.com/image.jpg");
    }

    @Test
    void testGetAllProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        // When
        List<ProductDTO> result = productService.getAllProducts();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Deck Box");
        verify(productRepository).findAll();
    }

    @Test
    void testGetProductById_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        ProductDTO result = productService.getProductById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Deck Box");
        assertThat(result.getCategory()).isEqualTo(ProductCategory.DECK_BOX);
        verify(productRepository).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
        verify(productRepository).findById(999L);
    }

    @Test
    void testGetProductsByCategory() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategory(ProductCategory.DECK_BOX)).thenReturn(products);

        // When
        List<ProductDTO> result = productService.getProductsByCategory(ProductCategory.DECK_BOX);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(ProductCategory.DECK_BOX);
        verify(productRepository).findByCategory(ProductCategory.DECK_BOX);
    }

    @Test
    void testSearchProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByNameContainingIgnoreCase("Deck")).thenReturn(products);

        // When
        List<ProductDTO> result = productService.searchProducts("Deck");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).containsIgnoringCase("Deck");
        verify(productRepository).findByNameContainingIgnoreCase("Deck");
    }

    @Test
    void testGetProductsInStock() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findProductsInStock(0)).thenReturn(products);

        // When
        List<ProductDTO> result = productService.getProductsInStock();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockQuantity()).isGreaterThan(0);
        verify(productRepository).findProductsInStock(0);
    }

    @Test
    void testCreateProduct() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductDTO result = productService.createProduct(testRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Deck Box");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("29.99"));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        CreateProductRequest updateRequest = new CreateProductRequest();
        updateRequest.setName("Updated Deck Box");
        updateRequest.setDescription("Updated description");
        updateRequest.setCategory(ProductCategory.DECK_BOX);
        updateRequest.setPrice(new BigDecimal("39.99"));
        updateRequest.setStockQuantity(50);

        // When
        ProductDTO result = productService.updateProduct(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.updateProduct(999L, testRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Given
        when(productRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
        verify(productRepository).existsById(999L);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateStock_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.updateStock(1L, 50);

        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateStock_NotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.updateStock(999L, 50))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }
}

