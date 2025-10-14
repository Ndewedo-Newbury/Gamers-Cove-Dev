package GamersCoveDev.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "friendships")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    private Long requesterId;

    private Long receiverId;


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

    // Constructor with UserEntity objects
    public FriendshipEntity(Long requester, Long receiver) {
        this.requesterId = requester;
        this.receiverId = receiver;
        this.status = FriendshipStatus.PENDING;
    }

    // Helper methods
    public boolean isAccepted() {
        return status == FriendshipStatus.ACCEPTED;
    }

    public boolean isPending() {
        return status == FriendshipStatus.PENDING;
    }

    public boolean isDeclined() {
        return status == FriendshipStatus.DECLINED;
    }

}