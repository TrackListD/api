package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tracklistd.api.Entity.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

}
