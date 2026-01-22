package com.tcgshop.review.service;

import com.tcgshop.review.model.Review;
import com.tcgshop.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
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
    void testCreateReview() {
        // Given
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        Review result = reviewService.createReview(testReview);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Great product!");
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void testGetReviewsByProductId() {
        // Given
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByProductId(1L)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getReviewsByProductId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        verify(reviewRepository).findByProductId(1L);
    }

    @Test
    void testGetAverageRating() {
        // Given
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(4.5);

        // When
        Double result = reviewService.getAverageRating(1L);

        // Then
        assertThat(result).isEqualTo(4.5);
        verify(reviewRepository).getAverageRatingByProductId(1L);
    }

    @Test
    void testGetAverageRating_NoReviews() {
        // Given
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(null);

        // When
        Double result = reviewService.getAverageRating(1L);

        // Then
        assertThat(result).isEqualTo(0.0);
        verify(reviewRepository).getAverageRatingByProductId(1L);
    }

    @Test
    void testGetReviewsByCustomerId() {
        // Given
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByCustomerId(1L)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getReviewsByCustomerId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
        verify(reviewRepository).findByCustomerId(1L);
    }

    @Test
    void testUpdateReview_Success() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review updateRequest = new Review();
        updateRequest.setRating(4);
        updateRequest.setComment("Updated comment");

        // When
        Review result = reviewService.updateReview(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void testUpdateReview_NotFound() {
        // Given
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        Review updateRequest = new Review();
        updateRequest.setRating(4);
        updateRequest.setComment("Updated comment");

        // When & Then
        assertThatThrownBy(() -> reviewService.updateReview(999L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Review not found");
        verify(reviewRepository).findById(999L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testDeleteReview_Success() {
        // Given
        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);

        // When
        reviewService.deleteReview(1L);

        // Then
        verify(reviewRepository).existsById(1L);
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void testDeleteReview_NotFound() {
        // Given
        when(reviewRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> reviewService.deleteReview(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Review not found");
        verify(reviewRepository).existsById(999L);
        verify(reviewRepository, never()).deleteById(anyLong());
    }
}

