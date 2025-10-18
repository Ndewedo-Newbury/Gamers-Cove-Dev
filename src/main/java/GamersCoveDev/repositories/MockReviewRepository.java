package GamersCoveDev.repositories;

import GamersCoveDev.domains.entities.ReviewEntity;
import GamersCoveDev.mockdata.mockreview;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MockReviewRepository provides an in-memory fake database of reviews.
 * It implements the ReviewRepository interface to support testing
 * without connecting to a real SQL or Mongo database.
 */
public class MockReviewRepository implements ReviewRepository {

    @Override
    public List<ReviewEntity> findByGameId(Long gameId) {
        return mockreview.REVIEWS.stream()
                .filter(r -> r.getGameId().equals(gameId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewEntity> findTop3ByGameIdOrderByRatingDesc(Long gameId) {
        return mockreview.REVIEWS.stream()
                .filter(r -> r.getGameId().equals(gameId))
                .sorted((a, b) -> Integer.compare(b.getRating(), a.getRating()))
                .limit(3)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewEntity> findByUserId(Long userId) {
        return mockreview.REVIEWS.stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // ---- Optional: mock findAll for debugging ----
    @Override
    public List<ReviewEntity> findAll() {
        return mockreview.REVIEWS;
    }

    @Override
    public List<ReviewEntity> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    // ---- Disable all unused JPA methods ----
    @Override public <S extends ReviewEntity> S save(S entity) { throw new UnsupportedOperationException(); }
    @Override public <S extends ReviewEntity> List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
    @Override public void deleteById(Long id) { throw new UnsupportedOperationException(); }
    @Override public void delete(ReviewEntity entity) { throw new UnsupportedOperationException(); }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends ReviewEntity> entities) {

    }

    @Override public void deleteAll() { throw new UnsupportedOperationException(); }
    @Override public long count() { return mockreview.REVIEWS.size(); }
    @Override public java.util.Optional<ReviewEntity> findById(Long id) {
        return mockreview.REVIEWS.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }
    @Override public boolean existsById(Long id) {
        return mockreview.REVIEWS.stream().anyMatch(r -> r.getId().equals(id));
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends ReviewEntity> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends ReviewEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<ReviewEntity> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public ReviewEntity getOne(Long aLong) {
        return null;
    }

    @Override
    public ReviewEntity getById(Long aLong) {
        return null;
    }

    @Override
    public ReviewEntity getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends ReviewEntity> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends ReviewEntity> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends ReviewEntity> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends ReviewEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ReviewEntity> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends ReviewEntity> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends ReviewEntity, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<ReviewEntity> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<ReviewEntity> findAll(Pageable pageable) {
        return null;
    }
}
