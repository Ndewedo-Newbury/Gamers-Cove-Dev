package GamersCoveDev.repositories;

import GamersCoveDev.domains.entities.FriendshipEntity;
import GamersCoveDev.domains.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends CrudRepository<FriendshipEntity, Long> {

    // Find friendship between two specific users (either direction)
    Optional<FriendshipEntity> findByRequesterAndReceiver(UserEntity requester, UserEntity receiver);

    // Find all friendships where user is requester or receiver
    List<FriendshipEntity> findByRequesterOrReceiver(UserEntity requester, UserEntity receiver);

    // Find pending friend requests received by a user
    List<FriendshipEntity> findByReceiverAndStatus(
            UserEntity receiver,
            FriendshipEntity.FriendshipStatus status
    );

    // Find pending friend requests sent by a user
    List<FriendshipEntity> findByRequesterAndStatus(
            UserEntity requester,
            FriendshipEntity.FriendshipStatus status
    );

    // Check if friendship exists with specific status
    boolean existsByRequesterAndReceiverAndStatus(
            UserEntity requester,
            UserEntity receiver,
            FriendshipEntity.FriendshipStatus status
    );

    // Find all accepted friendships for a user
    List<FriendshipEntity> findByRequesterAndStatusOrReceiverAndStatus(
            UserEntity requester1,
            FriendshipEntity.FriendshipStatus status1,
            UserEntity receiver2,
            FriendshipEntity.FriendshipStatus status2
    );
}