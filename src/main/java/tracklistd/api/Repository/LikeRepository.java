package tracklistd.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tracklistd.api.Entity.User;

import tracklistd.api.Entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPublicationId(Long userId, Long publicationId);

    void deleteByUserIdAndPublicationId(Long userId, Long publicationId);

    Long countByPublicationId(Long publicationId);

    @Query("SELECT l.user FROM Like l WHERE l.publication.id = :publicationId")
    List<User> findUsersByPublicationId(@Param("publicationId") Long publicationId);

}
