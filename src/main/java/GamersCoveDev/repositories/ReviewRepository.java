package GamersCoveDev.repositories;

import GamersCoveDev.domain.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {
       // Find all reviews for a specific game
      List<ReviewEntity> findbyGameId(Long gameId);
    // Find all reviews for a specific game
    List<ReviewEntity> findTop3ByGameIdOrderByRatingDesc(Long gameId);

}
