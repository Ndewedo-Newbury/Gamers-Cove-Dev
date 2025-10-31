package GamersCoveDev.repositories;

import GamersCoveDev.domains.entities.ReviewEntity;
import GamersCoveDev.mockdata.mockreview;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MockReviewRepository implements ReviewRepository {

    @Override
    public List<ReviewEntity> findByGameId(Long gameId) {
        return mockreview.REVIEWS.stream()
                .filter(r -> r.getGameId().equals(gameId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewEntity> findByUserId(Long userId) {
        return mockreview.REVIEWS.stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewEntity> findByGameIdAndUserId(Long gameId, Long userId) {
        return mockreview.REVIEWS.stream()
                .filter(r -> r.getGameId().equals(gameId) && r.getUserId().equals(userId))
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
    public <S extends ReviewEntity> S save(S entity) { throw new UnsupportedOperationException(); }
    @Override
    public <S extends ReviewEntity> Iterable<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
    @Override
    public Optional<ReviewEntity> findById(Long id) {
        return mockreview.REVIEWS.stream().filter(r -> r.getId().equals(id)).findFirst();
    }
    @Override
    public boolean existsById(Long id) {
        return mockreview.REVIEWS.stream().anyMatch(r -> r.getId().equals(id));
    }
    @Override
    public Iterable<ReviewEntity> findAll() { return mockreview.REVIEWS; }
    @Override
    public Iterable<ReviewEntity> findAllById(Iterable<Long> ids) { return List.of(); }
    @Override
    public long count() { return mockreview.REVIEWS.size(); }
    @Override
    public void deleteById(Long id) { throw new UnsupportedOperationException(); }
    @Override
    public void delete(ReviewEntity entity) { throw new UnsupportedOperationException(); }
    @Override
    public void deleteAllById(Iterable<? extends Long> ids) { throw new UnsupportedOperationException(); }
    @Override
    public void deleteAll(Iterable<? extends ReviewEntity> entities) { throw new UnsupportedOperationException(); }
    @Override
    public void deleteAll() { throw new UnsupportedOperationException(); }
}
