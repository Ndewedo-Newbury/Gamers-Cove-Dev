package GamersCoveDev.repositories;

import GamersCoveDev.domain.entities.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<GameEntity,Long> {
    //  Find game by its title (case-insensitive)
    Optional<GameEntity> findByTitleIgnoreCase(String title);

    // Find games by a specific genre (search in the comma-separated genres field)
    List<GameEntity> findByGenresContainingIgnoreCase(String genre);

    //  Find games by platform (similar to genre)
    List<GameEntity> findByPlatformsContainingIgnoreCase(String platform);

    //  Optional: list multiple games by their IDs
    List<GameEntity> findByIdIn(List<Long> ids);
}
