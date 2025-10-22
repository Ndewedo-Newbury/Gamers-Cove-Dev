package GamersCoveDev.repositories;

import GamersCoveDev.domains.entities.GameEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameRepository extends CrudRepository<GameEntity, Long> {
    Optional<GameEntity> findByTitle(String title);
    Optional<GameEntity> findByExternalApiId(String externalApiId);
}
