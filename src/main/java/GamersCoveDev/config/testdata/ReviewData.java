package GamersCoveDev.config.testdata;

import GamersCoveDev.domains.entities.ReviewEntity;
import GamersCoveDev.repositories.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile({"dev", "test"})
@Order(4)
public class ReviewData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ReviewData.class);
    private final ReviewRepository reviewRepository;

    public ReviewData(ReviewRepository reviewRepository) { this.reviewRepository = reviewRepository;}

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== INITIALIZING SAMPLE DATA (Profile: dev/test) ===");
        if (reviewRepository.count() == 0) {
            createSampleReviews();
            logger.info("User sample data created successfully!");
        } else {
            logger.info("Database already has user sample, skipping initialization");
        }

        logger.info("================================================");
    }

    private void createSampleReviews() {

        ReviewEntity review1 = ReviewEntity.builder()
                .userId(1L)
                .gameId(1L)
                .rating(5)
                .content("Game was alright, upscaling on p5 was crazyyy")
                .createdAt(LocalDateTime.now())
                .build();

        ReviewEntity review2 = ReviewEntity.builder()
                .userId(1L)
                .gameId(2L)
                .rating(7)
                .content("lovely game, wish i could experience it for the first time again!!")
                .createdAt(LocalDateTime.now())
                .build();

        ReviewEntity review3 = ReviewEntity.builder()
                .userId(2L)
                .gameId(2L)
                .rating(1)
                .content("wouldnt recommend that game to my worst enemey!!")
                .createdAt(LocalDateTime.now())
                .build();

        ReviewEntity review4 = ReviewEntity.builder()
                .userId(2L)
                .gameId(1L)
                .rating(9)
                .content("solid game!!")
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review1);
        logger.info("Created user: " + review1.getContentPreview());

        reviewRepository.save(review2);
        logger.info("Created user: " + review2.getContentPreview());

        reviewRepository.save(review3);
        logger.info("Created user: " + review3.getContentPreview());

        reviewRepository.save(review4);
        logger.info("Created user: " + review4.getContentPreview());
    }
}
