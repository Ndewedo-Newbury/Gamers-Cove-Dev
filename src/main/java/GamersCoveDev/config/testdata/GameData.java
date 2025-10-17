package GamersCoveDev.config.testdata;

import GamersCoveDev.domains.entities.GameEntity;
import GamersCoveDev.repositories.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile({"dev", "test"})
@Order(2)
public class GameData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(GameData.class);
    private final GameRepository gameRepository;

    public GameData(GameRepository gr) {
        this.gameRepository = gr;
    }
    @Override
    public void run(String... args) throws Exception {

        logger.info("=== INITIALIZING SAMPLE DATA (Profile: dev/test) ===");

        if  (gameRepository.count() == 0) {
            createSampleGames();
            logger.info("Game sample data created successfully!");
        } else {
            logger.info("Database already has game sample, skipping initialization");
        }
        logger.info("================================================");
    }

    private void createSampleGames() {
        LocalDate releaseDate = LocalDate.now();

        GameEntity game1 = GameEntity.builder()
                .title("Dying Light: The Beast")
                .coverImageUrl(".com")
                .externalApiId("something")
                .description("better than dying light 2")
                .build();

        game1.setGenres(new String[]{"zombie", "first-person", "parkour"});
        game1.setReleaseDate(releaseDate);
        game1.setPlatforms(new String[]{"Nintendo Switch", "PC"});

        gameRepository.save(game1);
        logger.info("Created game: " + game1.getTitle());

        GameEntity game2 = GameEntity.builder()
                .title("Minecraft")
                .coverImageUrl("microsoft.jpeg.com")
                .externalApiId("api id for best game ever")
                .description("also better than dying light 2")
                .build();

        game2.setGenres(new String[]{"sandbox", "survival", "fun"});
        game2.setReleaseDate(releaseDate);
        game2.setPlatforms(new String[]{"Nintendo Switch", "PC"});

        gameRepository.save(game2);
        logger.info("Created game: " + game2.getTitle());

    }
}
