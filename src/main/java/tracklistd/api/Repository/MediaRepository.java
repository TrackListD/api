package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tracklistd.api.Entity.Media;

import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, String> {
    @Query("""
            SELECT DISTINCT med FROM Media med
            LEFT JOIN FETCH med.authors
            LEFT JOIN FETCH med.musics
            WHERE med.spotifyID = :spotifyID
            """)
    Optional<Media> findMediaBySpotifyID(@Param("spotifyID") String spotifyID);

}
