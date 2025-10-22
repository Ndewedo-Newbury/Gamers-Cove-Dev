package GamersCoveDev.repositories;

import GamersCoveDev.domains.entities.ReviewEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends CrudRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByGameId(Long gameId);

    List<ReviewEntity> findByUserId(Long userId);

    // Optional: Find reviews by game and user (to prevent duplicate reviews)
    List<ReviewEntity> findByGameIdAndUserId(Long gameId, Long userId);
}
