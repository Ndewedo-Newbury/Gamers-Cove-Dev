package GamersCoveDev.domains.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Friendship table holds all the relationships in the database between users.
 * If a user is a friend to another user (Friendship Status = Accepted)
 * then friend can view user's gamertags
 */
@Data
@Entity
@Table(name = "friendships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"requester_id", "receiver_id"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private UserEntity requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver;

    @Column(name = "created_at", updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private java.time.LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private FriendshipStatus status = FriendshipStatus.PENDING;

    public enum FriendshipStatus {
        PENDING("pending"),
        ACCEPTED("accepted"),
        DECLINED("declined");

        private final String value;

        FriendshipStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public FriendshipEntity(UserEntity requester, UserEntity receiver) {
        this.requester = requester;
        this.receiver = receiver;
        this.status = FriendshipStatus.PENDING;
    }

    public Long getRequesterId() {
        return requester != null ? requester.getId() : null;
    }

    public Long getReceiverId() {
        return receiver != null ? receiver.getId() : null;
    }

    public boolean isAccepted() {
        return status == FriendshipStatus.ACCEPTED;
    }

    public boolean isPending() {
        return status == FriendshipStatus.PENDING;
    }

    public boolean isDeclined() {
        return status == FriendshipStatus.DECLINED;
    }

    // NEW: Helper method to check if a user is part of this friendship
    public boolean involvesUser(Long userId) {
        return (requester != null && requester.getId().equals(userId)) ||
                (receiver != null && receiver.getId().equals(userId));
    }

    // NEW: Get the other user in the friendship
    public UserEntity getOtherUser(Long userId) {
        if (requester != null && requester.getId().equals(userId)) {
            return receiver;
        } else if (receiver != null && receiver.getId().equals(userId)) {
            return requester;
        }
        return null;
    }
}