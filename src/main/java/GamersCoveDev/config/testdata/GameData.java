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

        // if  (gameRepository.count() == 0) {
        if (true) {
            createSampleGames();
            logger.info("Game sample data created successfully!");
        } else {
            logger.info("Database already has game sample, skipping initialization");
        }
        logger.info("================================================");
    }

    private void createSampleGames() {
        LocalDate releaseDate = LocalDate.now();

        // Game 1: Dying Light: The Beast
        if (!gameRepository.findByExternalApiId("something").isPresent()) {
            GameEntity game1 = GameEntity.builder()
                    .title("Dying Light: The Beast")
                    .name("Dying Light: The Beast")
                    .coverImageUrl(".com")
                    .externalApiId("something")
                    .description("better than dying light 2")
                    .build();

            game1.setGenres(new String[]{"zombie", "first-person", "parkour"});
            game1.setReleaseDate(releaseDate);
            game1.setPlatforms(new String[]{"Nintendo Switch", "PC"});

            gameRepository.save(game1);
            logger.info("Created game: " + game1.getTitle());
        } else {
            logger.info("Game 'Dying Light: The Beast' already exists, skipping");
        }

        // Game 2: Minecraft
        if (!gameRepository.findByExternalApiId("api id for best game ever").isPresent()) {
            GameEntity game2 = GameEntity.builder()
                    .title("Minecraft")
                    .name("Minecraft")
                    .coverImageUrl("microsoft.jpeg.com")
                    .externalApiId("api id for best game ever")
                    .description("also better than dying light 2")
                    .build();

            game2.setGenres(new String[]{"sandbox", "survival", "fun"});
            game2.setReleaseDate(releaseDate);
            game2.setPlatforms(new String[]{"Nintendo Switch", "PC"});

            gameRepository.save(game2);
            logger.info("Created game: " + game2.getTitle());
        } else {
            logger.info("Game 'Minecraft' already exists, skipping");
        }

        // Game 3: The Legend of Zelda: Breath of the Wild
        if (!gameRepository.findByExternalApiId("zelda-botw-001").isPresent()) {
            GameEntity game3 = GameEntity.builder()
                    .title("The Legend of Zelda: Breath of the Wild")
                    .name("The Legend of Zelda: Breath of the Wild")
                    .coverImageUrl("https://example.com/zelda-cover.jpg")
                    .externalApiId("zelda-botw-001")
                    .description("An open-world action-adventure game")
                    .build();

            game3.setGenres(new String[]{"action", "adventure", "open-world"});
            game3.setReleaseDate(LocalDate.of(2017, 3, 3));
            game3.setPlatforms(new String[]{"Nintendo Switch", "Wii U"});

            gameRepository.save(game3);
            logger.info("Created game: " + game3.getTitle());
        } else {
            logger.info("Game 'The Legend of Zelda: Breath of the Wild' already exists, skipping");
        }

        // Game 4: Elden Ring
        if (!gameRepository.findByExternalApiId("elden-ring-001").isPresent()) {
            GameEntity game4 = GameEntity.builder()
                    .title("Elden Ring")
                    .name("Elden Ring")
                    .coverImageUrl("https://example.com/elden-ring-cover.jpg")
                    .externalApiId("elden-ring-001")
                    .description("An action RPG from FromSoftware")
                    .build();

            game4.setGenres(new String[]{"action", "RPG", "fantasy"});
            game4.setReleaseDate(LocalDate.of(2022, 2, 25));
            game4.setPlatforms(new String[]{"PC", "PlayStation", "Xbox"});

            gameRepository.save(game4);
            logger.info("Created game: " + game4.getTitle());
        } else {
            logger.info("Game 'Elden Ring' already exists, skipping");
        }

    }
}
