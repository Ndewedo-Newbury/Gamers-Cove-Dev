package GamersCoveDev.config.testdata;

import GamersCoveDev.domains.entities.FriendshipEntity;
import GamersCoveDev.domains.entities.UserEntity;
import GamersCoveDev.repositories.FriendshipRepository;
import GamersCoveDev.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile({"dev", "test"})
@Order(3)
public class FriendshipData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipData.class);
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipData(FriendshipRepository fr, UserRepository ur) {
        this.friendshipRepository = fr;
        this.userRepository = ur;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== INITIALIZING SAMPLE DATA (Profile: dev/test) ===");
        if (friendshipRepository.count() == 0) {
            createSampleFriendships();
            logger.info("User sample data created successfully!");
        } else {
            logger.info("Database already has user sample, skipping initialization");
        }

        logger.info("================================================");
    }

    private void createSampleFriendships() {
        // Fetch users from database
        Optional<UserEntity> user1Opt = userRepository.findById(1L);
        Optional<UserEntity> user2Opt = userRepository.findById(2L);
        Optional<UserEntity> user3Opt = userRepository.findById(3L);

        if (user1Opt.isEmpty() || user2Opt.isEmpty() || user3Opt.isEmpty()) {
            logger.warn("Users not found in database. Make sure UserData runs before FriendshipData.");
            return;
        }

        UserEntity user1 = user1Opt.get(); // zelda_fan
        UserEntity user2 = user2Opt.get(); // fps_master
        UserEntity user3 = user3Opt.get(); // rpg_lover

        // Friendship 1: zelda_fan and rpg_lover are ACCEPTED friends
        // This means zelda_fan (user1) can see rpg_lover's (user3) gamertags if set to FRIENDS
        FriendshipEntity friendship1 = FriendshipEntity.builder()
                .requester(user1)
                .receiver(user3)
                .status(FriendshipEntity.FriendshipStatus.ACCEPTED)
                .build();

        friendshipRepository.save(friendship1);
        logger.info("Created ACCEPTED friendship: {} <-> {}",
                user1.getUsername(), user3.getUsername());

        // Friendship 2: fps_master sent PENDING request to zelda_fan
        // zelda_fan can accept or decline this
        FriendshipEntity friendship2 = FriendshipEntity.builder()
                .requester(user2)
                .receiver(user1)
                .status(FriendshipEntity.FriendshipStatus.PENDING)
                .build();

        friendshipRepository.save(friendship2);
        logger.info("Created PENDING friendship: {} -> {} (waiting for response)",
                user2.getUsername(), user1.getUsername());

        // Friendship 3: fps_master and rpg_lover are ACCEPTED friends
        FriendshipEntity friendship3 = FriendshipEntity.builder()
                .requester(user2)
                .receiver(user3)
                .status(FriendshipEntity.FriendshipStatus.ACCEPTED)
                .build();

        friendshipRepository.save(friendship3);
        logger.info("Created ACCEPTED friendship: {} <-> {}",
                user2.getUsername(), user3.getUsername());

        logger.info("========================================");
        logger.info("Friendship Summary:");
        logger.info("  ✓ {} and {} are friends (ACCEPTED)", user1.getUsername(), user3.getUsername());
        logger.info("  ⏳ {} waiting for {} to accept (PENDING)", user2.getUsername(), user1.getUsername());
        logger.info("  ✓ {} and {} are friends (ACCEPTED)", user2.getUsername(), user3.getUsername());
        logger.info("========================================");
    }
}
