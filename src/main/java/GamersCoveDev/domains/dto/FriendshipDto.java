package GamersCoveDev.domains.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipDto {
    private Long id;
    private Long requesterId;
    private String requesterUsername;
    private Long receiverId;
    private String receiverUsername;
    private String status;
    private LocalDateTime createdAt;
}