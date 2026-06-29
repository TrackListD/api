package tracklistd.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tracklistd.api.Entity.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    
    @Query("SELECT a FROM Album a JOIN a.authors artist WHERE artist.spotifyID = :spotifyId")
    List<Album> findAlbumsByArtistId(@Param("spotifyId") String spotifyId);
}
