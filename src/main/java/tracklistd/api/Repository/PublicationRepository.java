package tracklistd.api.Repository;

import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    public List<Publication> findByAuthorInOrderByCreatedAtDesc(Set<User> following);

    @Query("""
            SELECT p FROM Publication p
            WHERE p.createdAt >= :date
            ORDER BY (p.likes + p.comments) DESC
            """)
    List<Publication> findTrending(@Param("date") LocalDateTime date);
}
