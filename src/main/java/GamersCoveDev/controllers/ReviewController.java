package GamersCoveDev.controllers;

import GamersCoveDev.domains.dto.ReviewDto;
import GamersCoveDev.domains.entities.ReviewEntity;
import GamersCoveDev.mappers.Mapper;
import GamersCoveDev.services.ReviewService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;
    private final Mapper<ReviewEntity, ReviewDto> reviewMapper;

    public ReviewController(ReviewService reviewService, Mapper<ReviewEntity, ReviewDto> reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    // Create a new review
    @PostMapping(path = "/reviews")
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewDto reviewDto) {
        logger.info("=== POST /api/reviews ===");
        logger.info("Creating review for game ID: {}, user ID: {}, rating: {}",
                reviewDto.getGameId(), reviewDto.getUserId(), reviewDto.getRating());

        try {
            ReviewEntity reviewEntity = reviewMapper.mapFrom(reviewDto);
            ReviewEntity savedReview = reviewService.createReview(reviewEntity);
            ReviewDto responseDto = reviewMapper.mapTo(savedReview);

            logger.info("Review created successfully with ID: {}", savedReview.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating review", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get a specific review by review ID
    @GetMapping(path = "/reviews/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable("id") Long id) {
        logger.info("=== GET /api/reviews/{} ===", id);
        logger.info("Fetching review with ID: {}", id);

        try {
            Optional<ReviewEntity> review = reviewService.findById(id);

            if (review.isPresent()) {
                ReviewDto reviewDto = reviewMapper.mapTo(review.get());
                logger.info("Review found for game ID: {}, rating: {}",
                        reviewDto.getGameId(), reviewDto.getRating());
                return ResponseEntity.ok(reviewDto);
            } else {
                logger.warn("Review not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching review by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all reviews for a specific game
    @GetMapping(path = "/reviews/games/{gameId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByGameId(@PathVariable("gameId") Long gameId) {
        logger.info("=== GET /api/reviews/games/{} ===", gameId);
        logger.info("Fetching all reviews for game ID: {}", gameId);

        try {
            List<ReviewEntity> reviews = reviewService.findByGameId(gameId);
            List<ReviewDto> reviewDtos = reviews.stream()
                    .map(reviewMapper::mapTo)
                    .collect(Collectors.toList());

            logger.info("Found {} reviews for game ID: {}", reviewDtos.size(), gameId);
            return ResponseEntity.ok(reviewDtos);
        } catch (Exception e) {
            logger.error("Error fetching reviews by game ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all reviews made by a specific user
    @GetMapping(path = "/reviews/users/{userId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByUserId(@PathVariable("userId") Long userId) {
        logger.info("=== GET /api/reviews/users/{} ===", userId);
        logger.info("Fetching all reviews by user ID: {}", userId);

        try {
            List<ReviewEntity> reviews = reviewService.findByUserId(userId);
            List<ReviewDto> reviewDtos = reviews.stream()
                    .map(reviewMapper::mapTo)
                    .collect(Collectors.toList());

            logger.info("Found {} reviews by user ID: {}", reviewDtos.size(), userId);
            return ResponseEntity.ok(reviewDtos);
        } catch (Exception e) {
            logger.error("Error fetching reviews by user ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // BONUS: Update a review
    @PutMapping(path = "/reviews/{id}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable("id") Long id,
            @Valid @RequestBody ReviewDto reviewDto) {
        logger.info("=== PUT /api/reviews/{} ===", id);
        logger.info("Updating review with ID: {}", id);

        try {
            Optional<ReviewEntity> existingReview = reviewService.findById(id);

            if (existingReview.isPresent()) {
                ReviewEntity reviewEntity = reviewMapper.mapFrom(reviewDto);
                reviewEntity.setId(id);

                ReviewEntity updatedReview = reviewService.updateReview(reviewEntity);
                ReviewDto responseDto = reviewMapper.mapTo(updatedReview);

                logger.info("Review updated successfully");
                return ResponseEntity.ok(responseDto);
            } else {
                logger.warn("Review not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating review", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // BONUS: Delete a review
    @DeleteMapping(path = "/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") Long id) {
        logger.info("=== DELETE /api/reviews/{} ===", id);
        logger.info("Deleting review with ID: {}", id);

        try {
            Optional<ReviewEntity> existingReview = reviewService.findById(id);

            if (existingReview.isPresent()) {
                reviewService.deleteReview(id);
                logger.info("Review deleted successfully");
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Review not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting review", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // BONUS: Get average rating for a game
    @GetMapping(path = "/reviews/games/{gameId}/average-rating")
    public ResponseEntity<Double> getAverageRatingForGame(@PathVariable("gameId") Long gameId) {
        logger.info("=== GET /api/reviews/games/{}/average-rating ===", gameId);

        try {
            Double averageRating = reviewService.getAverageRatingForGame(gameId);
            logger.info("Average rating for game ID {}: {}", gameId, averageRating);
            return ResponseEntity.ok(averageRating);
        } catch (Exception e) {
            logger.error("Error calculating average rating", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}