package GamersCoveDev.services.impl;

import GamersCoveDev.domains.entities.UserEntity;
import GamersCoveDev.repositories.UserRepository;
import GamersCoveDev.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        logger.info("=== CREATE USER REQUEST ===");
        logger.info("Creating user with Firebase UID: {}", userEntity.getFirebaseUid());
        logger.info("Username: {}", userEntity.getUsername());
        logger.info("Email: {}", userEntity.getEmail());
        logger.info("Bio: {}", userEntity.getBio());
        logger.info("Avatar URL: {}", userEntity.getAvatarUrl());

        // FIXED: Reference the method correctly
        if (userEntity.getPreferredPlatforms() != null && userEntity.getPreferredPlatforms().length > 0) {
            logger.info("Preferred Platforms: {}", String.join(", ", userEntity.getPreferredPlatforms()));
        }

        // FIXED: Changed from getFavoriteGames() to getFavoriteGameIds()
        if (userEntity.getFavoriteGameIds() != null && userEntity.getFavoriteGameIds().length > 0) {
            logger.info("Favorite Game IDs: {}", Arrays.toString(userEntity.getFavoriteGameIds()));
        }

        logger.info("Gamertags: {}", userEntity.getGamertags());
        logger.info("Gamertags Visibility: {}", userEntity.getGamertagsVisibility());

        // IMPROVEMENT: Validate required fields before saving
        validateUser(userEntity);

        UserEntity savedUser = userRepository.save(userEntity);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        logger.info("============================");

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> findByFirebaseUid(String firebaseUid) {
        logger.info("=== FIND USER BY FIREBASE UID ===");
        logger.info("Searching for user with Firebase UID: {}", firebaseUid);

        Optional<UserEntity> user = userRepository.findByFirebaseUid(firebaseUid);
        if (user.isPresent()) {
            logger.info("User found: {}", user.get().getUsername());
        } else {
            logger.info("No user found with Firebase UID: {}", firebaseUid);
        }
        logger.info("=================================");

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> findByUsername(String username) {
        logger.info("=== FIND USER BY USERNAME ===");
        logger.info("Searching for user with username: {}", username);

        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            logger.info("User found with ID: {}", user.get().getId());
        } else {
            logger.info("No user found with username: {}", username);
        }
        logger.info("==============================");

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> findById(Long id) {
        logger.info("=== FIND USER BY ID ===");
        logger.info("Searching for user with ID: {}", id);

        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.info("User found: {}", user.get().getUsername());
        } else {
            logger.info("No user found with ID: {}", id);
        }
        logger.info("=======================");

        return user;
    }

    @Override
    public UserEntity updateUser(UserEntity userEntity) {
        logger.info("=== UPDATE USER REQUEST ===");
        logger.info("Updating user with ID: {}", userEntity.getId());
        logger.info("New data - Username: {}", userEntity.getUsername());
        logger.info("New data - Bio: {}", userEntity.getBio());

        // IMPROVEMENT: Verify user exists before updating
        if (userEntity.getId() == null || !userRepository.existsById(userEntity.getId())) {
            logger.error("Cannot update user: User with ID {} does not exist", userEntity.getId());
            throw new IllegalArgumentException("User does not exist");
        }

        UserEntity updatedUser = userRepository.save(userEntity);
        logger.info("User updated successfully");
        logger.info("===========================");

        return updatedUser;
    }

    // NEW: Validation method
    private void validateUser(UserEntity userEntity) {
        if (userEntity.getFirebaseUid() == null || userEntity.getFirebaseUid().trim().isEmpty()) {
            throw new IllegalArgumentException("Firebase UID cannot be null or empty");
        }
        if (userEntity.getUsername() == null || userEntity.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        // Check for duplicate username
        if (userRepository.findByUsername(userEntity.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + userEntity.getUsername());
        }

        // Check for duplicate Firebase UID
        if (userRepository.findByFirebaseUid(userEntity.getFirebaseUid()).isPresent()) {
            throw new IllegalArgumentException("User with this Firebase UID already exists");
        }
    }

    // NEW: Additional helper method for updating specific fields
    @Override
    public UserEntity updateUserProfile(Long userId, String bio, String avatarUrl,
                                        String[] preferredPlatforms, Long[] favoriteGameIds) {
        logger.info("=== UPDATE USER PROFILE ===");
        logger.info("Updating profile for user ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (bio != null) {
            user.setBio(bio);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        if (preferredPlatforms != null) {
            user.setPreferredPlatforms(preferredPlatforms);
        }
        if (favoriteGameIds != null) {
            user.setFavoriteGameIds(favoriteGameIds);
        }

        UserEntity updatedUser = userRepository.save(user);
        logger.info("Profile updated successfully");
        logger.info("===========================");

        return updatedUser;
    }

    // NEW: Update gamertag visibility
    @Override
    public UserEntity updateGamertagsVisibility(Long userId, UserEntity.GamertagsVisibility visibility) {
        logger.info("=== UPDATE GAMERTAGS VISIBILITY ===");
        logger.info("Updating gamertags visibility for user ID: {} to {}", userId, visibility);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setGamertagsVisibility(visibility);

        UserEntity updatedUser = userRepository.save(user);
        logger.info("Gamertags visibility updated successfully");
        logger.info("===================================");

        return updatedUser;
    }

    // NEW: Manage gamertags
    @Override
    public UserEntity addGamertag(Long userId, String platform, String gamertag) {
        logger.info("=== ADD GAMERTAG ===");
        logger.info("Adding gamertag for user ID: {}, Platform: {}, Gamertag: {}", userId, platform, gamertag);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.addGamertag(platform, gamertag);

        UserEntity updatedUser = userRepository.save(user);
        logger.info("Gamertag added successfully");
        logger.info("====================");

        return updatedUser;
    }

    @Override
    public UserEntity removeGamertag(Long userId, String platform) {
        logger.info("=== REMOVE GAMERTAG ===");
        logger.info("Removing gamertag for user ID: {}, Platform: {}", userId, platform);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.removeGamertag(platform);

        UserEntity updatedUser = userRepository.save(user);
        logger.info("Gamertag removed successfully");
        logger.info("=======================");

        return updatedUser;
    }
}