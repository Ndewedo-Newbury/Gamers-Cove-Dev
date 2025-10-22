package GamersCoveDev.repositories;

import GamersCoveDev.domains.entities.GameEntity;
import GamersCoveDev.mockdata.mockgames;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * MockGameRepository provides a lightweight in-memory repository
 * for testing AI tools without connecting to a real database.
 */
public class MockGameRepository implements GameRepository {

    @Override
    public List<GameEntity> findAll() {
        return mockgames.GAMES;
    }

    @Override
    public Optional<GameEntity> findByTitleIgnoreCase(String title) {
        return mockgames.GAMES.stream()
                .filter(g -> g.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    @Override
    public List<GameEntity> findByGenresContainingIgnoreCase(String genre) {
        return List.of();
    }

    @Override
    public List<GameEntity> findByPlatformsContainingIgnoreCase(String platform) {
        return List.of();
    }

    @Override
    public List<GameEntity> findByIdIn(List<Long> ids) {
        return List.of();
    }

    // Optionally, you can add more mock filters later:
    // e.g., findByGenresContainingIgnoreCase or findByPlatformsContainingIgnoreCase

    // ---- Disable all unused JPA methods ----
    @Override public <S extends GameEntity> S save(S entity) { throw new UnsupportedOperationException(); }
    @Override public <S extends GameEntity> List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
    @Override public Optional<GameEntity> findById(Long id) { throw new UnsupportedOperationException(); }
    @Override public boolean existsById(Long id) { throw new UnsupportedOperationException(); }
    @Override public List<GameEntity> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
    @Override public long count() { throw new UnsupportedOperationException(); }
    @Override public void deleteById(Long id) { throw new UnsupportedOperationException(); }
    @Override public void delete(GameEntity entity) { throw new UnsupportedOperationException(); }
    @Override public void deleteAllById(Iterable<? extends Long> ids) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll(Iterable<? extends GameEntity> entities) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll() { throw new UnsupportedOperationException(); }

    @Override
    public void flush() {

    }

    @Override
    public <S extends GameEntity> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends GameEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<GameEntity> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public GameEntity getOne(Long aLong) {
        return null;
    }

    @Override
    public GameEntity getById(Long aLong) {
        return null;
    }

    @Override
    public GameEntity getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends GameEntity> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends GameEntity> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends GameEntity> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends GameEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends GameEntity> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends GameEntity> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends GameEntity, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<GameEntity> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<GameEntity> findAll(Pageable pageable) {
        return null;
    }

    // You can also leave all paging/sorting variants unimplemented:
    // They are irrelevant for offline mock testing.
}
