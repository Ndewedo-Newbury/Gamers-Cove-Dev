package GamersCoveDev.services;

import GamersCoveDev.domains.entities.GameEntity;

import java.util.List;
import java.util.Optional;

public interface GameService {
    GameEntity createGameEntity(GameEntity gameEntity);
    Optional<GameEntity> findById(Long id);
    Optional<GameEntity> findTitle(String title);
    List<GameEntity> findAll();
    Optional<GameEntity> findByExternalApiId(String externalApiId);
}
