package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaListRepository extends JpaRepository<MediaList, Long> {

    // Métodos CRUD básicos já vêm inclusos (save, findById, findAll, delete, etc.)

    // Método que retorna uma lista de um Usuario
    Optional<MediaList> findMediaListByAuthor(Long userId);

    // Método que retorna todas as Listas de um User
    List<MediaList> findAllByAuthor(User author);

    // Método que retorna todas as Listas de um User a depender da Privacidade
    List<MediaList> findAllByAuthorAndWhoCanSee(User author, Privacy whoCanSee);

    // Método que retorna a qtd numerica de listas criadas por um User
    Long countByAuthorId(Long authorId);

    // Método que retorna todas as Listas favoritas de um User
    List<MediaList> findAllByAuthorAndIsFavorite(User author, Boolean isFavorite);

    // Método que retorna uma lista com um nome, para verificar Unicidade
    Optional<MediaList> findMediaListByAuthorAndListName(User author, String listName);

    @Query("""
            SELECT DISTINCT m FROM MediaList m
            JOIN FETCH m.author
            LEFT JOIN FETCH m.media med
            LEFT JOIN FETCH med.authors
            WHERE m.id = :id
            """)
    Optional<MediaList> findByIdWithAuthorAndMedia(@Param("id") Long id);

    // Query 2: só inicializa tags (ElementCollection) no MESMO id.
    // Não pode ser fetch-joinada junto com 'media' na query acima porque
    // ambas são coleções no mesmo nível (filhas diretas de MediaList) —
    // o Hibernate não permite (ou multiplica linhas de forma incorreta).
    @Query("""
            SELECT DISTINCT m FROM MediaList m
            LEFT JOIN FETCH m.tags
            WHERE m.id = :id
            """)
    Optional<MediaList> findByIdWithTags(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT m FROM MediaList m
            JOIN FETCH m.author
            LEFT JOIN FETCH m.media med
            LEFT JOIN FETCH med.authors
            WHERE m.author = :author
            """)
    List<MediaList> findAllByAuthorWithAuthorAndMedia(@Param("author") User author);

    @Query("""
            SELECT DISTINCT m FROM MediaList m
            LEFT JOIN FETCH m.tags
            WHERE m.author = :author
            """)
    List<MediaList> findAllByAuthorWithTags(@Param("author") User author);
}
