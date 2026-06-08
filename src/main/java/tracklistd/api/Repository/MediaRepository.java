package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tracklistd.api.Entity.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
}
