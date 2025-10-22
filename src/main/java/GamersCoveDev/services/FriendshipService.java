package GamersCoveDev.services;

import GamersCoveDev.domains.entities.FriendshipEntity;
import GamersCoveDev.domains.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface FriendshipService {

    // Send a friend request
    FriendshipEntity sendFriendRequest(Long requesterId, Long receiverId);

    // Accept a friend request
    FriendshipEntity acceptFriendRequest(Long friendshipId, Long userId);

    // Decline a friend request
    FriendshipEntity declineFriendRequest(Long friendshipId, Long userId);

    // Remove/unfriend
    void removeFriendship(Long friendshipId, Long userId);

    // Check if two users are friends
    boolean areFriends(Long userId1, Long userId2);

    // Check if gamertags should be visible
    boolean canViewGamertags(Long profileUserId, Long requestingUserId,
                             UserEntity.GamertagsVisibility visibility);

    // Get all friends for a user
    List<UserEntity> getFriends(Long userId);

    // Get pending friend requests received
    List<FriendshipEntity> getPendingReceivedRequests(Long userId);

    // Get pending friend requests sent
    List<FriendshipEntity> getPendingSentRequests(Long userId);

    // Find friendship by ID
    Optional<FriendshipEntity> findById(Long id);
}