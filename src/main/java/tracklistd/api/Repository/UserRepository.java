package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tracklistd.api.Entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdLoginApi(String idLoginApi);

    boolean existsByIdLoginApi(String idLoginApi);

    @EntityGraph(attributePaths = {
            "followers",
            "following",
            "favoriteAlbum",
            "favoriteMusic",
            "favoriteArtist"
    })
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findFullById(Long id);
}