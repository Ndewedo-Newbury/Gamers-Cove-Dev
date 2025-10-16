package GamersCoveDev.mappers.impl;

import GamersCoveDev.domains.dto.ReviewDto;
import GamersCoveDev.domains.entities.ReviewEntity;
import GamersCoveDev.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapperImpl implements Mapper<ReviewEntity, ReviewDto> {

    @Override
    public ReviewDto mapTo(ReviewEntity reviewEntity) {
        return ReviewDto.builder()
                .id(reviewEntity.getId())
                .userId(reviewEntity.getUserId())
                .gameId(reviewEntity.getGameId())
                .rating(reviewEntity.getRating())
                .content(reviewEntity.getContent())
                .createdAt(reviewEntity.getCreatedAt())
                .build();
    }

    @Override
    public ReviewEntity mapFrom(ReviewDto reviewDto) {
        return ReviewEntity.builder()
                .id(reviewDto.getId())
                .userId(reviewDto.getUserId())
                .gameId(reviewDto.getGameId())
                .rating(reviewDto.getRating())
                .content(reviewDto.getContent())
                .createdAt(reviewDto.getCreatedAt())
                .build();
    }
}