package tracklistd.api.Repository;

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

    // Busca o usuario com favoriteAlbum, favoriteMusic e favoriteArtist
    // inicializados como objetos (sem LazyInitializationException ao
    // acessar os campos diretamente). As colecoes lazy aninhadas dentro
    // desses objetos (favoriteAlbum.authors, favoriteAlbum.musics,
    // favoriteMusic.authors) nao cabem nesta query - cada uma e uma bag
    // e o Hibernate nao permite fetch de multiplas bags na mesma query
    // (MultipleBagFetchException). Elas sao resolvidas em queries
    // dedicadas: fetchFavoriteAlbumAuthors, fetchFavoriteAlbumMusics,
    // fetchUserWithFavoriteMusicAuthors.
    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.favoriteAlbum
            LEFT JOIN FETCH u.favoriteMusic
            LEFT JOIN FETCH u.favoriteArtist
            WHERE u.id = :id
            """)
    Optional<User> findFullById(@Param("id") Long id);

    // Reidrata o favoriteAlbum do usuario com authors inicializado.
    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.favoriteAlbum fa
            LEFT JOIN FETCH fa.authors
            WHERE u.id = :userId
            """)
    Optional<User> fetchFavoriteAlbumAuthors(@Param("userId") Long userId);

    // Reidrata o favoriteAlbum do usuario com musics inicializado
    // (necessario para Album.getTotalDurationMs(), usado pelo MediaMinDTO).
    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.favoriteAlbum fa
            LEFT JOIN FETCH fa.musics
            WHERE u.id = :userId
            """)
    Optional<User> fetchFavoriteAlbumMusics(@Param("userId") Long userId);

    // Reidrata o usuario com favoriteMusic.authors inicializado.
    // O JPQL exige que o dono da associacao fetch-joined (u) esteja no
    // SELECT - nao o alias da propria associacao (fm) - por isso o
    // retorno e User, nao Music. Chamar logo apos findFullById e usar
    // result.getFavoriteMusic() para popular o objeto antes de mapear
    // para DTO.
    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.favoriteMusic fm
            LEFT JOIN FETCH fm.authors
            WHERE u.id = :userId
            """)
    Optional<User> fetchUserWithFavoriteMusicAuthors(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM User u JOIN u.followers f WHERE u.id = :userId")
    Long countFollowersByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM User u JOIN u.following f WHERE u.id = :userId")
    Long countFollowingByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM User u JOIN u.following f WHERE u.id = :followerId AND f.id = :followedId")
    boolean existsByFollowerIdAndFollowedId(@Param("followerId") Long followerId, @Param("followedId") Long followedId);
}