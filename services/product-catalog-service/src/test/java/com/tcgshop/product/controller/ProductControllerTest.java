package com.tcgshop.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcgshop.product.dto.CreateProductRequest;
import com.tcgshop.product.dto.ProductDTO;
import com.tcgshop.product.model.ProductCategory;
import com.tcgshop.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductDTO testProductDTO;
    private CreateProductRequest testRequest;

    @BeforeEach
    void setUp() {
        testProductDTO = new ProductDTO(
                1L,
                "Test Deck Box",
                "A test deck box",
                ProductCategory.DECK_BOX,
                new BigDecimal("29.99"),
                100,
                "https://example.com/image.jpg",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        testRequest = new CreateProductRequest();
        testRequest.setName("Test Deck Box");
        testRequest.setDescription("A test deck box");
        testRequest.setCategory(ProductCategory.DECK_BOX);
        testRequest.setPrice(new BigDecimal("29.99"));
        testRequest.setStockQuantity(100);
        testRequest.setImageUrl("https://example.com/image.jpg");
    }

    @Test
    void testGetAllProducts() throws Exception {
        // Given
        List<ProductDTO> products = Arrays.asList(testProductDTO);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Deck Box"));

        verify(productService).getAllProducts();
    }

    @Test
    void testGetProductById() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(testProductDTO);

        // When & Then
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Deck Box"))
                .andExpect(jsonPath("$.category").value("DECK_BOX"));

        verify(productService).getProductById(1L);
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        // Given
        List<ProductDTO> products = Arrays.asList(testProductDTO);
        when(productService.getProductsByCategory(ProductCategory.DECK_BOX)).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/products/category/DECK_BOX"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("DECK_BOX"));

        verify(productService).getProductsByCategory(ProductCategory.DECK_BOX);
    }

    @Test
    void testSearchProducts() throws Exception {
        // Given
        List<ProductDTO> products = Arrays.asList(testProductDTO);
        when(productService.searchProducts("Deck")).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/products/search").param("q", "Deck"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(productService).searchProducts("Deck");
    }

    @Test
    void testGetProductsInStock() throws Exception {
        // Given
        List<ProductDTO> products = Arrays.asList(testProductDTO);
        when(productService.getProductsInStock()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/products/stock"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(productService).getProductsInStock();
    }

    @Test
    void testCreateProduct() throws Exception {
        // Given
        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(testProductDTO);

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Deck Box"));

        verify(productService).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Given
        when(productService.updateProduct(eq(1L), any(CreateProductRequest.class))).thenReturn(testProductDTO);

        // When & Then
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Deck Box"));

        verify(productService).updateProduct(eq(1L), any(CreateProductRequest.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void testCreateProduct_ValidationError() throws Exception {
        // Given: Invalid request (missing required fields)
        CreateProductRequest invalidRequest = new CreateProductRequest();
        // Missing name, category, price, stockQuantity

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any());
    }
}

