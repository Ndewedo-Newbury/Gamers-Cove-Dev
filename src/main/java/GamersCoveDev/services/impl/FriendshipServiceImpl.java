package GamersCoveDev.services.impl;

import GamersCoveDev.domains.entities.FriendshipEntity;
import GamersCoveDev.domains.entities.UserEntity;
import GamersCoveDev.repositories.FriendshipRepository;
import GamersCoveDev.repositories.UserRepository;
import GamersCoveDev.services.FriendshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FriendshipServiceImpl implements FriendshipService {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipServiceImpl.class);

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipServiceImpl(FriendshipRepository friendshipRepository,
                                 UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    @Override
    public FriendshipEntity sendFriendRequest(Long requesterId, Long receiverId) {
        logger.info("=== SEND FRIEND REQUEST ===");
        logger.info("Requester ID: {}, Receiver ID: {}", requesterId, receiverId);

        // Validate users exist
        UserEntity requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        UserEntity receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // Check if users are the same
        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        // Check if friendship already exists (either direction)
        Optional<FriendshipEntity> existing1 = friendshipRepository
                .findByRequesterAndReceiver(requester, receiver);
        Optional<FriendshipEntity> existing2 = friendshipRepository
                .findByRequesterAndReceiver(receiver, requester);

        if (existing1.isPresent() || existing2.isPresent()) {
            throw new IllegalArgumentException("Friendship request already exists");
        }

        // Create new friendship request
        FriendshipEntity friendship = FriendshipEntity.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendshipEntity.FriendshipStatus.PENDING)
                .build();

        FriendshipEntity saved = friendshipRepository.save(friendship);
        logger.info("Friend request sent successfully, ID: {}", saved.getId());
        return saved;
    }

    @Override
    public FriendshipEntity acceptFriendRequest(Long friendshipId, Long userId) {
        logger.info("=== ACCEPT FRIEND REQUEST ===");
        logger.info("Friendship ID: {}, User ID: {}", friendshipId, userId);

        FriendshipEntity friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found"));

        // Only receiver can accept
        if (!friendship.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("Only the receiver can accept this request");
        }

        // Check if already accepted
        if (friendship.isAccepted()) {
            throw new IllegalArgumentException("Friend request already accepted");
        }

        friendship.setStatus(FriendshipEntity.FriendshipStatus.ACCEPTED);
        FriendshipEntity updated = friendshipRepository.save(friendship);

        logger.info("Friend request accepted successfully");
        return updated;
    }

    @Override
    public FriendshipEntity declineFriendRequest(Long friendshipId, Long userId) {
        logger.info("=== DECLINE FRIEND REQUEST ===");
        logger.info("Friendship ID: {}, User ID: {}", friendshipId, userId);

        FriendshipEntity friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found"));

        // Only receiver can decline
        if (!friendship.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("Only the receiver can decline this request");
        }

        friendship.setStatus(FriendshipEntity.FriendshipStatus.DECLINED);
        FriendshipEntity updated = friendshipRepository.save(friendship);

        logger.info("Friend request declined successfully");
        return updated;
    }

    @Override
    public void removeFriendship(Long friendshipId, Long userId) {
        logger.info("=== REMOVE FRIENDSHIP ===");
        logger.info("Friendship ID: {}, User ID: {}", friendshipId, userId);

        FriendshipEntity friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found"));

        // Either user can remove the friendship
        if (!friendship.involvesUser(userId)) {
            throw new IllegalArgumentException("You are not part of this friendship");
        }

        friendshipRepository.delete(friendship);
        logger.info("Friendship removed successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean areFriends(Long userId1, Long userId2) {
        if (userId1.equals(userId2)) {
            return true; // User is always "friends" with themselves
        }

        UserEntity user1 = userRepository.findById(userId1).orElse(null);
        UserEntity user2 = userRepository.findById(userId2).orElse(null);

        if (user1 == null || user2 == null) {
            return false;
        }

        // Check both directions
        return friendshipRepository.existsByRequesterAndReceiverAndStatus(
                user1, user2, FriendshipEntity.FriendshipStatus.ACCEPTED
        ) || friendshipRepository.existsByRequesterAndReceiverAndStatus(
                user2, user1, FriendshipEntity.FriendshipStatus.ACCEPTED
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canViewGamertags(Long profileUserId, Long requestingUserId,
                                    UserEntity.GamertagsVisibility visibility) {
        // Owner can always see their own gamertags
        if (profileUserId.equals(requestingUserId)) {
            return true;
        }

        // Public gamertags are visible to everyone
        if (visibility == UserEntity.GamertagsVisibility.PUBLIC) {
            return true;
        }

        // Friends-only: check friendship status
        if (visibility == UserEntity.GamertagsVisibility.FRIENDS) {
            return areFriends(profileUserId, requestingUserId);
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getFriends(Long userId) {
        logger.info("=== GET FRIENDS ===");
        logger.info("User ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<FriendshipEntity> friendships = friendshipRepository
                .findByRequesterAndStatusOrReceiverAndStatus(
                        user, FriendshipEntity.FriendshipStatus.ACCEPTED,
                        user, FriendshipEntity.FriendshipStatus.ACCEPTED
                );

        List<UserEntity> friends = friendships.stream()
                .map(friendship -> friendship.getOtherUser(userId))
                .collect(Collectors.toList());

        logger.info("Found {} friends", friends.size());
        return friends;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendshipEntity> getPendingReceivedRequests(Long userId) {
        logger.info("=== GET PENDING RECEIVED REQUESTS ===");
        logger.info("User ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<FriendshipEntity> requests = friendshipRepository
                .findByReceiverAndStatus(user, FriendshipEntity.FriendshipStatus.PENDING);

        logger.info("Found {} pending received requests", requests.size());
        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendshipEntity> getPendingSentRequests(Long userId) {
        logger.info("=== GET PENDING SENT REQUESTS ===");
        logger.info("User ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<FriendshipEntity> requests = friendshipRepository
                .findByRequesterAndStatus(user, FriendshipEntity.FriendshipStatus.PENDING);

        logger.info("Found {} pending sent requests", requests.size());
        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FriendshipEntity> findById(Long id) {
        return friendshipRepository.findById(id);
    }
}