package tracklistd.api.Repository;

import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Privacy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    public List<Publication> findByAuthorInAndWhoCanSeeInOrderByPublicationDateDesc(Set<User> following, List<Privacy> privacies);

    public List<Publication> findByAuthorIdAndWhoCanSeeInOrderByPublicationDateDesc(Long authorId, List<Privacy> privacies);

    @Query("""
            SELECT p FROM Publication p
            WHERE p.publicationDate >= :date
              AND p.whoCanSee = 'PUBLIC'
            ORDER BY (SIZE(p.likes) + SIZE(p.comments)) DESC
            """)
    List<Publication> findTrending(@Param("date") LocalDateTime date);

    @Override
    Optional<Publication> findById(Long idPost);

}
