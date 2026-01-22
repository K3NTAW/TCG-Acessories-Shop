package com.tcgshop.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcgshop.review.model.Review;
import com.tcgshop.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    private Review testReview;

    @BeforeEach
    void setUp() {
        testReview = new Review();
        testReview.setId(1L);
        testReview.setProductId(1L);
        testReview.setCustomerId(1L);
        testReview.setRating(5);
        testReview.setComment("Great product!");
        testReview.setCreatedAt(LocalDateTime.now());
        testReview.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateReview() throws Exception {
        // Given
        when(reviewService.createReview(any(Review.class))).thenReturn(testReview);

        // When & Then
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReview)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Great product!"));

        verify(reviewService).createReview(any(Review.class));
    }

    @Test
    void testGetReviewsByProduct() throws Exception {
        // Given
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewService.getReviewsByProductId(1L)).thenReturn(reviews);

        // When & Then
        mockMvc.perform(get("/reviews/product/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].productId").value(1));

        verify(reviewService).getReviewsByProductId(1L);
    }

    @Test
    void testGetAverageRating() throws Exception {
        // Given
        when(reviewService.getAverageRating(1L)).thenReturn(4.5);

        // When & Then
        mockMvc.perform(get("/reviews/product/1/average"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.averageRating").value(4.5));

        verify(reviewService).getAverageRating(1L);
    }

    @Test
    void testGetReviewsByCustomer() throws Exception {
        // Given
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewService.getReviewsByCustomerId(1L)).thenReturn(reviews);

        // When & Then
        mockMvc.perform(get("/reviews/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerId").value(1));

        verify(reviewService).getReviewsByCustomerId(1L);
    }

    @Test
    void testUpdateReview() throws Exception {
        // Given
        testReview.setRating(4);
        testReview.setComment("Updated comment");
        when(reviewService.updateReview(eq(1L), any(Review.class))).thenReturn(testReview);

        // When & Then
        mockMvc.perform(put("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReview)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Updated comment"));

        verify(reviewService).updateReview(eq(1L), any(Review.class));
    }

    @Test
    void testDeleteReview() throws Exception {
        // Given
        doNothing().when(reviewService).deleteReview(1L);

        // When & Then
        mockMvc.perform(delete("/reviews/1"))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(1L);
    }
}

