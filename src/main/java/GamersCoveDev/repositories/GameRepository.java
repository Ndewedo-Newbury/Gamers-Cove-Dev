package GamersCoveDev.repositories;

import GamersCoveDev.domains.entities.GameEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<GameEntity, Long> {

    // Match both ReviewTool and RecommendationTool
    Optional<GameEntity> findByTitleIgnoreCase(String title);

    Optional<GameEntity> findByExternalApiId(String externalApiId);
    Optional<GameEntity> findByTitle(String title);
    List<GameEntity> findByIdIn(List<Long> ids);
    List<GameEntity> findAll(); // already inherited but declared for clarity
}
