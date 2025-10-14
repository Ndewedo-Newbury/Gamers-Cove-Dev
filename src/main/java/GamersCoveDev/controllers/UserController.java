package GamersCoveDev.controllers;

import GamersCoveDev.domains.dto.UserDto;
import GamersCoveDev.domains.entities.UserEntity;
import GamersCoveDev.mappers.Mapper;
import GamersCoveDev.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

        import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final Mapper<UserEntity, UserDto> userMapper;

    public UserController(UserService userService,
                          Mapper<UserEntity, UserDto> userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping(path = "/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) {
        logger.info("=== POST /api/users ENDPOINT CALLED ===");
        logger.info("Received UserDto: {}", user);
        logger.info("======================================");

        try {
            UserEntity userEntity = userMapper.mapFrom(user);
            UserEntity savedUserEntity = userService.createUser(userEntity);
            UserDto responseDto = userMapper.mapTo(savedUserEntity);

            logger.info("User created successfully with ID: {}", savedUserEntity.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(path = "/users/me")
    public ResponseEntity<UserDto> updateUserInfo(
            @RequestBody UserDto user,
            @AuthenticationPrincipal String firebaseUid) {
        logger.info("=== PUT /api/users/me ENDPOINT CALLED ===");
        logger.info("Received request to update user info: {}", user);
        logger.info("========================================");

        try {
            String uidToUpdate = firebaseUid != null ? firebaseUid : user.getFirebaseUid();

            Optional<UserEntity> existingUser = userService.findByFirebaseUid(uidToUpdate);
            if (existingUser.isPresent()) {
                UserEntity userEntity = userMapper.mapFrom(user);
                userEntity.setId(existingUser.get().getId());
                userEntity.setFirebaseUid(existingUser.get().getFirebaseUid()); // Prevent UID changes

                UserEntity updatedUser = userService.updateUser(userEntity);
                UserDto updatedUserDto = userMapper.mapTo(updatedUser);

                logger.info("User updated successfully: {}", updatedUserDto.getUsername());
                return ResponseEntity.ok(updatedUserDto);
            } else {
                logger.warn("User not found for update with Firebase UID: {}", uidToUpdate);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/users/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(
            @PathVariable("username") String username,
            @AuthenticationPrincipal Long currentUserId) {
        logger.info("=== GET /api/users/username/{} ENDPOINT CALLED ===", username);
        logger.info("Fetching user by username: {}", username);
        logger.info("Requesting user ID: {}", currentUserId);
        logger.info("=================================================");

        try {
            Optional<UserEntity> userFound = userService.findByUsername(username);

            if (userFound.isPresent()) {
                UserEntity userEntity = userFound.get();
                UserDto userDto = userMapper.mapTo(userEntity);
                return ResponseEntity.ok(userDto);
            } else {
                logger.warn("****************************************************");
                logger.warn("User not found with username: {}", username);
                logger.warn("****************************************************");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching user by username", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/users/{userId}/favorite_games")
    public ResponseEntity<Long[]> getUserFavoriteGames(@PathVariable("userId") Long userId) {
        logger.info("=== GET /api/users/{}/favorite_games ENDPOINT CALLED ===", userId);
        logger.info("Fetching favorite games for user ID: {}", userId);
        logger.info("======================================================");

        try {
            Optional<UserEntity> user = userService.findById(userId);
            if (user.isPresent()) {
                Long[] favoriteGameIds = user.get().getFavoriteGameIds();
                logger.info("Found {} favorite games for user {}",
                        favoriteGameIds != null ? favoriteGameIds.length : 0,
                        user.get().getUsername());
                if (favoriteGameIds != null && favoriteGameIds.length > 0) {
                    logger.info("Favorite game IDs: {}", java.util.Arrays.toString(favoriteGameIds));
                }
                return ResponseEntity.ok(favoriteGameIds != null ? favoriteGameIds : new Long[0]);
            } else {
                logger.warn("User not found with ID: {}", userId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching favorite games", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // NEW: Update gamertag visibility
    @PatchMapping(path = "/users/me/gamertags-visibility")
    public ResponseEntity<UserDto> updateGamertagsVisibility(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal String firebaseUid) {
        logger.info("=== PATCH /api/users/me/gamertags-visibility ENDPOINT CALLED ===");
        logger.info("Updating gamertags visibility");
        logger.info("================================================================");

        try {
            String visibilityStr = request.get("visibility");
            if (visibilityStr == null) {
                return ResponseEntity.badRequest().build();
            }

            UserEntity.GamertagsVisibility visibility =
                    UserEntity.GamertagsVisibility.valueOf(visibilityStr.toUpperCase());

            Optional<UserEntity> user = userService.findByFirebaseUid(firebaseUid);
            if (user.isPresent()) {
                UserEntity updatedUser = userService.updateGamertagsVisibility(
                        user.get().getId(),
                        visibility
                );
                UserDto userDto = userMapper.mapTo(updatedUser);
                logger.info("Gamertags visibility updated to: {}", visibility);
                return ResponseEntity.ok(userDto);
            } else {
                logger.warn("User not found with Firebase UID: {}", firebaseUid);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid visibility value: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating gamertags visibility", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // NEW: Add gamertag
    @PostMapping(path = "/users/me/gamertags")
    public ResponseEntity<UserDto> addGamertag(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal String firebaseUid) {
        logger.info("=== POST /api/users/me/gamertags ENDPOINT CALLED ===");
        logger.info("Adding gamertag");
        logger.info("====================================================");

        try {
            String platform = request.get("platform");
            String gamertag = request.get("gamertag");

            if (platform == null || gamertag == null) {
                return ResponseEntity.badRequest().build();
            }

            Optional<UserEntity> user = userService.findByFirebaseUid(firebaseUid);
            if (user.isPresent()) {
                UserEntity updatedUser = userService.addGamertag(
                        user.get().getId(),
                        platform,
                        gamertag
                );
                UserDto userDto = userMapper.mapTo(updatedUser);
                logger.info("Gamertag added successfully for platform: {}", platform);
                return ResponseEntity.ok(userDto);
            } else {
                logger.warn("User not found with Firebase UID: {}", firebaseUid);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error adding gamertag", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // NEW: Remove gamertag
    @DeleteMapping(path = "/users/me/gamertags/{platform}")
    public ResponseEntity<UserDto> removeGamertag(
            @PathVariable("platform") String platform,
            @AuthenticationPrincipal String firebaseUid) {
        logger.info("=== DELETE /api/users/me/gamertags/{} ENDPOINT CALLED ===", platform);
        logger.info("Removing gamertag for platform: {}", platform);
        logger.info("=========================================================");

        try {
            Optional<UserEntity> user = userService.findByFirebaseUid(firebaseUid);
            if (user.isPresent()) {
                UserEntity updatedUser = userService.removeGamertag(
                        user.get().getId(),
                        platform
                );
                UserDto userDto = userMapper.mapTo(updatedUser);
                logger.info("Gamertag removed successfully for platform: {}", platform);
                return ResponseEntity.ok(userDto);
            } else {
                logger.warn("User not found with Firebase UID: {}", firebaseUid);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error removing gamertag", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // NEW: Update user profile (partial update)
    @PatchMapping(path = "/users/me/profile")
    public ResponseEntity<UserDto> updateUserProfile(
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal String firebaseUid) {
        logger.info("=== PATCH /api/users/me/profile ENDPOINT CALLED ===");
        logger.info("Updating user profile");
        logger.info("===================================================");

        try {
            Optional<UserEntity> user = userService.findByFirebaseUid(firebaseUid);
            if (user.isPresent()) {
                String bio = (String) updates.get("bio");
                String avatarUrl = (String) updates.get("avatarUrl");
                String[] preferredPlatforms = updates.containsKey("preferredPlatforms")
                        ? ((java.util.List<String>) updates.get("preferredPlatforms")).toArray(new String[0])
                        : null;
                Long[] favoriteGameIds = updates.containsKey("favoriteGameIds")
                        ? ((java.util.List<Number>) updates.get("favoriteGameIds"))
                        .stream()
                        .map(Number::longValue)
                        .toArray(Long[]::new)
                        : null;

                UserEntity updatedUser = userService.updateUserProfile(
                        user.get().getId(),
                        bio,
                        avatarUrl,
                        preferredPlatforms,
                        favoriteGameIds
                );

                UserDto userDto = userMapper.mapTo(updatedUser);
                logger.info("User profile updated successfully");
                return ResponseEntity.ok(userDto);
            } else {
                logger.warn("User not found with Firebase UID: {}", firebaseUid);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error updating user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}