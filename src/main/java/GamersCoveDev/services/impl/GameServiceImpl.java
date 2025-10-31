package GamersCoveDev.services.impl;

import GamersCoveDev.domains.entities.GameEntity;
import GamersCoveDev.repositories.GameRepository;
import GamersCoveDev.services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.Optional;
import java.util.List;


@Service
public class GameServiceImpl implements GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GameEntity createGameEntity(GameEntity gameEntity) {
        try {
            logger.info("=== CREATE GAME REQUEST ===");
            logger.info("Creating game with external API ID: {}", gameEntity.getExternalApiId());
            logger.info("Title: {}", gameEntity.getTitle());
            logger.info("Description: {}", gameEntity.getDescription());
            logger.info("Cover Image URL: {}", gameEntity.getCoverImageUrl());
            logger.info("Release Date: {}", gameEntity.getReleaseDate());

            if (gameEntity.getPlatforms() != null) {
                logger.info("Platforms: {}", String.join(", ", gameEntity.getPlatforms()));
            }

            if (gameEntity.getGenres() != null) {
                logger.info("Genres: {}", String.join(", ", gameEntity.getGenres()));
            }

            // Check if game with the same externalApiId already exists
            Optional<GameEntity> existingGame = gameRepository.findByExternalApiId(gameEntity.getExternalApiId());
            if (existingGame.isPresent()) {
                logger.info("Game with external API ID {} already exists. Updating...", gameEntity.getExternalApiId());
                GameEntity existing = existingGame.get();
                existing.setTitle(gameEntity.getTitle());
                existing.setDescription(gameEntity.getDescription());
                existing.setCoverImageUrl(gameEntity.getCoverImageUrl());
                existing.setReleaseDate(gameEntity.getReleaseDate());
                existing.setPlatforms(gameEntity.getPlatforms());
                existing.setGenres(gameEntity.getGenres());
                
                GameEntity updatedGame = gameRepository.save(existing);
                logger.info("Game updated successfully with ID: {}", updatedGame.getId());
                logger.info("============================");
                return updatedGame;
            } else {
                // Create new game
                GameEntity newGame = new GameEntity();
                newGame.setExternalApiId(gameEntity.getExternalApiId());
                newGame.setTitle(gameEntity.getTitle());
                newGame.setDescription(gameEntity.getDescription());
                newGame.setCoverImageUrl(gameEntity.getCoverImageUrl());
                newGame.setReleaseDate(gameEntity.getReleaseDate());
                newGame.setPlatforms(gameEntity.getPlatforms());
                newGame.setGenres(gameEntity.getGenres());
                
                GameEntity savedGame = gameRepository.save(newGame);
                logger.info("Game created successfully with ID: {}", savedGame.getId());
                logger.info("============================");
                return savedGame;
            }
        } catch (Exception e) {
            logger.error("Error in createGameEntity: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<GameEntity> findById(Long id) {
        logger.info("=== FIND GAME BY ID ===");
        logger.info("Searching for game with ID: {}", id);

        Optional<GameEntity> foundGame = gameRepository.findById(id);
        if (foundGame.isPresent()) {
            logger.info("Game found: {}", foundGame.get().getTitle());
        } else {
            logger.info("No game found with ID: {}", id);
        }
        logger.info("=======================");

        return foundGame;
    }

    @Override
    public Optional<GameEntity> findTitle(String title) {
        logger.info("=== FIND GAME BY TITLE ===");
        logger.info("Searching for game with title: {}", title);

        Optional<GameEntity> foundGame = gameRepository.findByTitle(title);
        if (foundGame.isPresent()) {
            logger.info("Game found with ID: {}", foundGame.get().getId());
        } else {
            logger.info("No game found with title: {}", title);
        }
        logger.info("==============================");

        return foundGame;
    }

    @Override
    public List<GameEntity> findAll() {
        logger.info("=== FIND ALL GAMES ===");

        List<GameEntity> games = (List<GameEntity>) gameRepository.findAll();
        logger.info("Found {} games in database", games.size());
        logger.info("======================");

        return games;
    }

    @Override
    public Optional<GameEntity> findByExternalApiId(String externalApiId) {
        logger.info("=== FIND GAME BY EXTERNAL API ID ===");
        logger.info("Searching for game with external API ID: {}", externalApiId);

        Optional<GameEntity> game = gameRepository.findByExternalApiId(externalApiId);

        if (game.isPresent()) {
            logger.info("Game found: {}", game.get().getTitle());
        } else {
            logger.info("No game found with external API ID: {}", externalApiId);
        }

        return game;
    }
}