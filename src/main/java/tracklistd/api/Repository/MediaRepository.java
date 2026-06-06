package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tracklistd.api.Entity.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
