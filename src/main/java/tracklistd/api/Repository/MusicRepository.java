package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tracklistd.api.Entity.Music;

@Repository
public interface MusicRepository extends JpaRepository<Music, String> {
}