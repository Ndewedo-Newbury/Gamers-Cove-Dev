package GamersCoveDev.services;

import GamersCoveDev.domains.entities.ReviewEntity;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    ReviewEntity createReview(ReviewEntity review);
    Optional<ReviewEntity> findById(Long id);
    List<ReviewEntity> findByGameId(Long gameId);
    List<ReviewEntity> findByUserId(Long userId);
    ReviewEntity updateReview(ReviewEntity review);
    void deleteReview(Long id);
    Double getAverageRatingForGame(Long gameId);
}