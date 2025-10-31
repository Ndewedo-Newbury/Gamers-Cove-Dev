package GamersCoveDev.repositories;

import GamersCoveDev.domains.entities.GameEntity;
import GamersCoveDev.mockdata.mockgames;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MockGameRepository implements GameRepository {

    @Override
    public Optional<GameEntity> findByTitleIgnoreCase(String title) {
        return mockgames.GAMES.stream()
                .filter(g -> g.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }
    @Override
    public Optional<GameEntity> findByTitle(String title) {
        return mockgames.GAMES.stream()
                .filter(g -> g.getTitle().equals(title))
                .findFirst();
    }

    @Override
    public Optional<GameEntity> findByExternalApiId(String externalApiId) {
        return mockgames.GAMES.stream()
                .filter(g -> g.getExternalApiId().equals(externalApiId))
                .findFirst();
    }

    @Override
    public List<GameEntity> findByIdIn(List<Long> ids) {
        return List.of();
    }

    @Override
    public List<GameEntity> findAll() {
        return mockgames.GAMES;
    }

    @Override
    public Iterable<GameEntity> findAllById(Iterable<Long> longs) {
        return null;
    }

    // --- Disable writes ---
    @Override
    public <S extends GameEntity> S save(S entity) {
        throw new UnsupportedOperationException("Mock repository is read-only.");
    }
    @Override
    public <S extends GameEntity> Iterable<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
    @Override
    public Optional<GameEntity> findById(Long id) {
        return mockgames.GAMES.stream().filter(g -> g.getId().equals(id)).findFirst();
    }
    @Override
    public boolean existsById(Long id) {
        return mockgames.GAMES.stream().anyMatch(g -> g.getId().equals(id));
    }
    @Override
    public long count() {
        return mockgames.GAMES.size();
    }
    @Override
    public void deleteById(Long id) { throw new UnsupportedOperationException(); }
    @Override
    public void delete(GameEntity entity) { throw new UnsupportedOperationException(); }
    @Override
    public void deleteAllById(Iterable<? extends Long> ids) { throw new UnsupportedOperationException(); }
    @Override
    public void deleteAll(Iterable<? extends GameEntity> entities) { throw new UnsupportedOperationException(); }
    @Override
    public void deleteAll() { throw new UnsupportedOperationException(); }
}
