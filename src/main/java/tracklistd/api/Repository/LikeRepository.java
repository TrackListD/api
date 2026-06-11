package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tracklistd.api.Entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPublicationId(Long userId, Long publicationId);

    void deleteByUserIdAndPublicationId(Long userId, Long publicationId);

    Long countByPublicationId(Long publicationId);

}
