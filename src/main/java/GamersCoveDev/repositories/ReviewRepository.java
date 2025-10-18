package GamersCoveDev.repositories;

import GamersCoveDev.domains.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    // Find all reviews for a specific game
    List<ReviewEntity> findByGameId(Long gameId);

    // Find top 3 reviews sorted by rating descending
    List<ReviewEntity> findTop3ByGameIdOrderByRatingDesc(Long gameId);

    // Optional: Find all reviews written by a specific user
    List<ReviewEntity> findByUserId(Long userId);
}
