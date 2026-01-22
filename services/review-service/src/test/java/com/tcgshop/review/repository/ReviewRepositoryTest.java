package com.tcgshop.review.repository;

import com.tcgshop.review.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private Review testReview1;
    private Review testReview2;

    @BeforeEach
    void setUp() {
        testReview1 = new Review();
        testReview1.setProductId(1L);
        testReview1.setCustomerId(1L);
        testReview1.setRating(5);
        testReview1.setComment("Excellent product!");
        testReview1.setCreatedAt(LocalDateTime.now());
        testReview1.setUpdatedAt(LocalDateTime.now());

        testReview2 = new Review();
        testReview2.setProductId(1L);
        testReview2.setCustomerId(2L);
        testReview2.setRating(4);
        testReview2.setComment("Good product");
        testReview2.setCreatedAt(LocalDateTime.now());
        testReview2.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testFindByProductId() {
        // Given
        Review saved1 = entityManager.persistAndFlush(testReview1);
        Review saved2 = entityManager.persistAndFlush(testReview2);

        // When
        List<Review> result = reviewRepository.findByProductId(1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Review::getProductId).containsOnly(1L);
    }

    @Test
    void testFindByCustomerId() {
        // Given
        Review saved1 = entityManager.persistAndFlush(testReview1);
        Review saved2 = entityManager.persistAndFlush(testReview2);

        // When
        List<Review> result = reviewRepository.findByCustomerId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
    }

    @Test
    void testGetAverageRatingByProductId() {
        // Given
        entityManager.persistAndFlush(testReview1); // Rating: 5
        entityManager.persistAndFlush(testReview2); // Rating: 4

        // When
        Double average = reviewRepository.getAverageRatingByProductId(1L);

        // Then
        assertThat(average).isEqualTo(4.5); // (5 + 4) / 2 = 4.5
    }

    @Test
    void testGetAverageRatingByProductId_NoReviews() {
        // When
        Double average = reviewRepository.getAverageRatingByProductId(999L);

        // Then
        assertThat(average).isNull();
    }

    @Test
    void testSaveReview() {
        // Given
        Review newReview = new Review();
        newReview.setProductId(2L);
        newReview.setCustomerId(3L);
        newReview.setRating(3);
        newReview.setComment("Average product");
        newReview.setCreatedAt(LocalDateTime.now());
        newReview.setUpdatedAt(LocalDateTime.now());

        // When
        Review saved = reviewRepository.save(newReview);
        entityManager.flush();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProductId()).isEqualTo(2L);
        assertThat(saved.getRating()).isEqualTo(3);
    }
}

