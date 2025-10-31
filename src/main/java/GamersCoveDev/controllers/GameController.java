package GamersCoveDev.controllers;

import GamersCoveDev.domains.dto.GameDto;
import GamersCoveDev.domains.entities.GameEntity;
import GamersCoveDev.mappers.Mapper;
import GamersCoveDev.services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;
    private final Mapper<GameEntity, GameDto> gameMapper;
    private final RestTemplate restTemplate;

       public GameController(GameService gameService, Mapper<GameEntity, GameDto> gameMapper, RestTemplate restTemplate) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
        this.restTemplate = restTemplate;
    }

    @GetMapping(path = "/games/{gameId}")
    public ResponseEntity<GameDto> getGameById(@PathVariable("gameId") Long gameId) {
        logger.info("=== GET /api/games/{} ===", gameId);

        Optional<GameEntity> game = gameService.findById(gameId);

        if (game.isPresent()) {
            GameDto gameDto = gameMapper.mapTo(game.get());
            logger.info("Found game: {}", gameDto.getTitle());
            return ResponseEntity.ok(gameDto);
        } else {
            logger.warn("Game not found with ID: {}", gameId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/games")
    public ResponseEntity<List<GameDto>> getAllGames() {
        logger.info("=== GET /api/games ===");
        logger.info("Fetching all games from database");

        try {
            List<GameEntity> games = gameService.findAll();
            List<GameDto> gameDtos = games.stream()
                    .map(gameMapper::mapTo)
                    .collect(java.util.stream.Collectors.toList());

            logger.info("Found {} games in database", gameDtos.size());
            return ResponseEntity.ok(gameDtos);
        } catch (Exception e) {
            logger.error("Error fetching all games: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = "/games")
    public ResponseEntity<GameDto> createGame(@RequestBody GameDto gameDto) {
        logger.info("=== POST /api/games ===");
        logger.info("Creating new game: {}", gameDto.getTitle());

        try {
            // Convert DTO to Entity
            GameEntity gameEntity = gameMapper.mapFrom(gameDto);
            
            // Save the game
            GameEntity savedGame = gameService.createGameEntity(gameEntity);
            
            // Convert back to DTO for the response
            GameDto savedGameDto = gameMapper.mapTo(savedGame);
            
            logger.info("Successfully created game with ID: {}", savedGame.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGameDto);
        } catch (Exception e) {
            logger.error("Error creating game: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}