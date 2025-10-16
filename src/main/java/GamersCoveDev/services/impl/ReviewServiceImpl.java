package GamersCoveDev.services.impl;

import GamersCoveDev.domains.entities.ReviewEntity;
import GamersCoveDev.repositories.ReviewRepository;
import GamersCoveDev.services.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewEntity createReview(ReviewEntity review) {
        logger.info("Creating review for game ID: {}, user ID: {}",
                review.getGameId(), review.getUserId());

        // Validation happens automatically via @Valid annotation
        ReviewEntity savedReview = reviewRepository.save(review);

        logger.info("Review created with ID: {}", savedReview.getId());
        return savedReview;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReviewEntity> findById(Long id) {
        logger.info("Finding review by ID: {}", id);
        return reviewRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewEntity> findByGameId(Long gameId) {
        logger.info("Finding reviews for game ID: {}", gameId);
        return reviewRepository.findByGameId(gameId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewEntity> findByUserId(Long userId) {
        logger.info("Finding reviews by user ID: {}", userId);
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public ReviewEntity updateReview(ReviewEntity review) {
        logger.info("Updating review ID: {}", review.getId());

        if (review.getId() == null || !reviewRepository.existsById(review.getId())) {
            throw new IllegalArgumentException("Review does not exist");
        }

        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long id) {
        logger.info("Deleting review ID: {}", id);
        reviewRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingForGame(Long gameId) {
        logger.info("Calculating average rating for game ID: {}", gameId);

        List<ReviewEntity> reviews = reviewRepository.findByGameId(gameId);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double average = reviews.stream()
                .mapToInt(ReviewEntity::getRating)
                .average()
                .orElse(0.0);

        return Math.round(average * 10.0) / 10.0; // Round to 1 decimal place
    }
}