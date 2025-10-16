package GamersCoveDev.controllers;

import GamersCoveDev.domains.dto.FriendshipDto;
import GamersCoveDev.domains.dto.UserDto;
import GamersCoveDev.domains.entities.FriendshipEntity;
import GamersCoveDev.domains.entities.UserEntity;
import GamersCoveDev.mappers.Mapper;
import GamersCoveDev.services.FriendshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipController.class);

    private final FriendshipService friendshipService;
    private final Mapper<FriendshipEntity, FriendshipDto> friendshipMapper;
    private final Mapper<UserEntity, UserDto> userMapper;

    public FriendshipController(FriendshipService friendshipService,
                                Mapper<FriendshipEntity, FriendshipDto> friendshipMapper,
                                Mapper<UserEntity, UserDto> userMapper) {
        this.friendshipService = friendshipService;
        this.friendshipMapper = friendshipMapper;
        this.userMapper = userMapper;
    }

    // Send a friend request
    @PostMapping
    public ResponseEntity<FriendshipDto> sendFriendRequest(
            @RequestBody Map<String, Long> request,
            @AuthenticationPrincipal Long currentUserId) {
        logger.info("=== POST /api/friendships ===");

        try {
            Long receiverId = request.get("receiverId");
            if (receiverId == null) {
                return ResponseEntity.badRequest().build();
            }

            FriendshipEntity friendship = friendshipService.sendFriendRequest(
                    currentUserId,
                    receiverId
            );

            FriendshipDto dto = friendshipMapper.mapTo(friendship);
            logger.info("Friend request sent successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            logger.error("Error sending friend request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Accept a friend request
    @PatchMapping("/{friendshipId}/accept")
    public ResponseEntity<FriendshipDto> acceptFriendRequest(
            @PathVariable Long friendshipId,
            @AuthenticationPrincipal Long currentUserId) {
        logger.info("=== PATCH /api/friendships/{}/accept ===", friendshipId);

        try {
            FriendshipEntity friendship = friendshipService.acceptFriendRequest(
                    friendshipId,
                    currentUserId
            );

            FriendshipDto dto = friendshipMapper.mapTo(friendship);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            logger.error("Error accepting friend request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Decline a friend request
    @PatchMapping("/{friendshipId}/decline")
    public ResponseEntity<FriendshipDto> declineFriendRequest(
            @PathVariable Long friendshipId,
            @AuthenticationPrincipal Long currentUserId) {
        logger.info("=== PATCH /api/friendships/{}/decline ===", friendshipId);

        try {
            FriendshipEntity friendship = friendshipService.declineFriendRequest(
                    friendshipId,
                    currentUserId
            );

            FriendshipDto dto = friendshipMapper.mapTo(friendship);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            logger.error("Error declining friend request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Remove/unfriend
    @DeleteMapping("/{friendshipId}")
    public ResponseEntity<Void> removeFriendship(
            @PathVariable Long friendshipId,
            @AuthenticationPrincipal Long currentUserId) {
        logger.info("=== DELETE /api/friendships/{} ===", friendshipId);

        try {
            friendshipService.removeFriendship(friendshipId, currentUserId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error removing friendship: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all friends
    @GetMapping("/friends")
    public ResponseEntity<List<UserDto>> getFriends(
            @AuthenticationPrincipal Long currentUserId) {
        logger.info("=== GET /api/friendships/friends ===");

        try {
            List<UserEntity> friends = friendshipService.getFriends(currentUserId);
            List<UserDto> friendDtos = friends.stream()
                    .map(userMapper::mapTo)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(friendDtos);
        } catch (Exception e) {
            logger.error("Error getting friends", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get pending received requests
    @GetMapping("/requests/received")
    public ResponseEntity<List<FriendshipDto>> getPendingReceivedRequests(
            @AuthenticationPrincipal Long currentUserId) {
        logger.info("=== GET /api/friendships/requests/received ===");

        try {
            List<FriendshipEntity> requests = friendshipService
                    .getPendingReceivedRequests(currentUserId);
            List<FriendshipDto> dtos = requests.stream()
                    .map(friendshipMapper::mapTo)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error getting pending received requests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get pending sent requests
    @GetMapping("/requests/sent")
    public ResponseEntity<List<FriendshipDto>> getPendingSentRequests(
            @AuthenticationPrincipal Long currentUserId) {
        logger.info("=== GET /api/friendships/requests/sent ===");

        try {
            List<FriendshipEntity> requests = friendshipService
                    .getPendingSentRequests(currentUserId);
            List<FriendshipDto> dtos = requests.stream()
                    .map(friendshipMapper::mapTo)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error getting pending sent requests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Check if two users are friends
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkFriendship(
            @RequestParam Long userId,
            @AuthenticationPrincipal Long currentUserId) {
        logger.info("=== GET /api/friendships/check?userId={} ===", userId);

        boolean areFriends = friendshipService.areFriends(currentUserId, userId);
        return ResponseEntity.ok(Map.of("areFriends", areFriends));
    }
}