package com.tcgshop.product.controller;

import com.tcgshop.product.dto.CreateProductRequest;
import com.tcgshop.product.model.ProductCategory;
import com.tcgshop.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProductById() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateProduct() throws Exception {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Test Product");
        request.setCategory(ProductCategory.DECK_BOX);
        request.setPrice(new BigDecimal("29.99"));
        request.setStockQuantity(100);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Product\",\"category\":\"DECK_BOX\",\"price\":29.99,\"stockQuantity\":100}"))
                .andExpect(status().isCreated());
    }
}

