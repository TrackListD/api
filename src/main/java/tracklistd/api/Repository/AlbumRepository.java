package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tracklistd.api.Entity.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

}
