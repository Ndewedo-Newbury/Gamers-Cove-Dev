package GamersCoveDev.domains.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private Long userId;
    private Long gameId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;

}
