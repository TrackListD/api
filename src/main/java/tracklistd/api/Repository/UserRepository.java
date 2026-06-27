package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tracklistd.api.Entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdLoginApi(String idLoginApi);

    boolean existsByIdLoginApi(String idLoginApi);

    @EntityGraph(attributePaths = {
            "favoriteAlbum",
            "favoriteMusic",
            "favoriteArtist"
    })
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findFullById(Long id);

    @Query("SELECT COUNT(f) FROM User u JOIN u.followers f WHERE u.id = :userId")
    Long countFollowersByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM User u JOIN u.following f WHERE u.id = :userId")
    Long countFollowingByUserId(@Param("userId") Long userId);


    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM User u JOIN u.following f WHERE u.id = :followerId AND f.id = :followedId")
    boolean existsByFollowerIdAndFollowedId(@Param("followerId") Long followerId, @Param("followedId") Long followedId);
}