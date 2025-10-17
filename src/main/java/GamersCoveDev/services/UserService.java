package GamersCoveDev.services;

import GamersCoveDev.domains.entities.UserEntity;

import java.util.Optional;

public interface UserService {

    UserEntity createUser(UserEntity userEntity);
    Optional<UserEntity> findByFirebaseUid(String firebaseUid);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findById(Long id);
    UserEntity updateUser(UserEntity userEntity);

    UserEntity updateUserProfile(Long userId, String bio, String avatarUrl,
                                 String[] preferredPlatforms, Long[] favoriteGameIds);
    UserEntity updateGamertagsVisibility(Long userId, UserEntity.GamertagsVisibility visibility);
    UserEntity addGamertag(Long userId, String platform, String gamertag);
    UserEntity removeGamertag(Long userId, String platform);
}
