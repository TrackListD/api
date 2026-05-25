package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tracklistd.api.Entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Integer countByPublicationId(Long publicationId); // query automatica que conta likes em uma publicação especifica
}
