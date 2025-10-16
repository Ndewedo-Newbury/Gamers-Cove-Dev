package GamersCoveDev.mappers.impl;

import GamersCoveDev.domains.dto.FriendshipDto;
import GamersCoveDev.domains.entities.FriendshipEntity;
import GamersCoveDev.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class FriendshipMapper implements Mapper<FriendshipEntity, FriendshipDto> {

    @Override
    public FriendshipDto mapTo(FriendshipEntity friendship) {
        return FriendshipDto.builder()
                .id(friendship.getId())
                .requesterId(friendship.getRequesterId())
                .requesterUsername(friendship.getRequester() != null ?
                        friendship.getRequester().getUsername() : null)
                .receiverId(friendship.getReceiverId())
                .receiverUsername(friendship.getReceiver() != null ?
                        friendship.getReceiver().getUsername() : null)
                .status(friendship.getStatus().getValue())
                .createdAt(friendship.getCreatedAt())
                .build();
    }

    @Override
    public FriendshipEntity mapFrom(FriendshipDto dto) {
        // Not typically used for friendships
        return FriendshipEntity.builder()
                .id(dto.getId())
                .status(FriendshipEntity.FriendshipStatus.valueOf(dto.getStatus().toUpperCase()))
                .build();
    }
}