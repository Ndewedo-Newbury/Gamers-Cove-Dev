package GamersCoveDev.config.testdata;

import GamersCoveDev.domains.entities.UserEntity;
import GamersCoveDev.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile({"dev", "test"})
@Order(1) // Run first
public class UserData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserData.class);
    private final UserRepository userRepository;

    public UserData(UserRepository ur) {
        this.userRepository = ur;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== INITIALIZING SAMPLE DATA (Profile: dev/test) ===");
        if (userRepository.count() == 0) {
            createSampleUsers();
            logger.info("User sample data created successfully!");
        } else {
            logger.info("Database already has user sample, skipping initialization");
        }

        logger.info("================================================");
    }

    private void createSampleUsers() {

        UserEntity user1 = UserEntity.builder()
                .firebaseUid("sample-firebase-uid-001")
                .username("zelda_fan")
                .email("zelda.fan@example.com")
                .password("password123")
                .bio("Nintendo enthusiast and Zelda speedrunner")
                .avatarUrl("https://example.com/zelda-avatar.jpg")
                .gamertagsVisibility(UserEntity.GamertagsVisibility.PUBLIC)
                .build();

        user1.setPreferredPlatforms(new String[]{"Nintendo Switch", "PC"});
        user1.setFavoriteGameIds(new Long[]{1L, 2L});

        Map<String, String> user1Gamertags = new HashMap<>();
        user1Gamertags.put("Nintendo", "zelda_speedrun");
        user1Gamertags.put("Steam", "zelda_fan_pc");
        user1.setGamertags(user1Gamertags);

        userRepository.save(user1);
        logger.info("Created user: " + user1.getUsername());

        UserEntity user2 = UserEntity.builder()
                .firebaseUid("sample-firebase-uid-002")
                .username("fps_master")
                .email("fps.master@example.com")
                .password("securepass456")
                .bio("Competitive FPS player, always looking for new challenges")
                .avatarUrl("https://example.com/fps-avatar.jpg")
                .gamertagsVisibility(UserEntity.GamertagsVisibility.FRIENDS)
                .build();

        user2.setPreferredPlatforms(new String[]{"PC", "PlayStation"});
        user2.setFavoriteGameIds(new Long[]{1L, 2L});

        Map<String, String> user2Gamertags = new HashMap<>();
        user2Gamertags.put("Steam", "fps_master_2024");
        user2Gamertags.put("PSN", "fps_master_ps");
        user2.setGamertags(user2Gamertags);

        userRepository.save(user2);
        logger.info("Created user: " + user2.getUsername());

        UserEntity user3 = UserEntity.builder()
                .firebaseUid("sample-firebase-uid-003")
                .username("rpg_lover")
                .email("rpg.lover@example.com")
                .password("fantasy789")
                .bio("JRPG enthusiast, love long story-driven games")
                .avatarUrl("https://example.com/rpg-avatar.jpg")
                .gamertagsVisibility(UserEntity.GamertagsVisibility.PUBLIC)
                .build();

        user3.setPreferredPlatforms(new String[]{"PlayStation", "Nintendo Switch", "PC"});
        user3.setFavoriteGameIds(new Long[]{1L, 2L});

        Map<String, String> user3Gamertags = new HashMap<>();
        user3Gamertags.put("PSN", "rpg_lover_final");
        user3Gamertags.put("Steam", "rpg_collector");
        user3Gamertags.put("Nintendo", "rpg_lover_switch");
        user3.setGamertags(user3Gamertags);

        userRepository.save(user3);
        logger.info("Created user: " + user3.getUsername());
    }
}
