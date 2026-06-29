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

    // OBS: 'musics' NÃO pode ser fetch-joinada junto com 'med.authors' na
    // mesma query — ambas são List (Bag) e o Hibernate lança
    // MultipleBagFetchException ao tentar fetch join de duas bags ao mesmo
    // tempo. Por isso o fetch de musics fica em uma query separada
    // (findByIdWithAlbumMusics), seguindo o mesmo padrão já usado para tags.
    @Query("""
            SELECT DISTINCT m FROM MediaList m
            JOIN FETCH m.author
            LEFT JOIN FETCH m.media med
            LEFT JOIN FETCH med.authors
            WHERE m.id = :id
            """)
    Optional<MediaList> findByIdWithAuthorAndMedia(@Param("id") Long id);

    // Query auxiliar: inicializa a coleção 'musics' de qualquer Album presente
    // na lista (mesmo id, mesma sessão — as entidades já carregadas por
    // findByIdWithAuthorAndMedia são as mesmas instâncias gerenciadas aqui).
    // Necessária porque Album.getTotalDurationMs() acessa 'musics', que é
    // FetchType.LAZY; sem isso, o acesso fora da sessão lança
    // LazyInitializationException durante a serialização da resposta.
    @Query("""
            SELECT DISTINCT m FROM MediaList m
            LEFT JOIN FETCH m.media med
            LEFT JOIN FETCH TREAT(med AS Album).musics
            WHERE m.id = :id
            """)
    Optional<MediaList> findByIdWithAlbumMusics(@Param("id") Long id);

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

    // Mesma lógica de findByIdWithAlbumMusics, mas para todas as listas de um
    // autor.
    @Query("""
            SELECT DISTINCT m FROM MediaList m
            LEFT JOIN FETCH m.media med
            LEFT JOIN FETCH TREAT(med AS Album).musics
            WHERE m.author = :author
            """)
    List<MediaList> findAllByAuthorWithAlbumMusics(@Param("author") User author);

    @Query("""
            SELECT DISTINCT m FROM MediaList m
            LEFT JOIN FETCH m.tags
            WHERE m.author = :author
            """)
    List<MediaList> findAllByAuthorWithTags(@Param("author") User author);
}