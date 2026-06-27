package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    // Métodos CRUD básicos já vêm inclusos (save, findById, findAll, delete, etc.)

    //Método de Unicidade de Avaliações
    Optional<Rating> findRatingByAuthorAndTarget(User author, Media target);

    // Método que retorna todas as Avaliações de um Usuario
    @EntityGraph(attributePaths = {"target"})
    List<Rating> findAllByAuthor(User author);

    // Método que busca todas as Avaliações de um Usuario a partir da privacidade
    @EntityGraph(attributePaths = {"target"})
    List<Rating> findRatingByAuthorAndWhoCanSee(User author, Privacy whoCanSee);

    //listagem de avaliações otimizada (O JOIN FETCH mata o problema do N+1)
    @Query("SELECT r FROM Rating r JOIN FETCH r.target t WHERE r.author.id = :authorId")
    List<Rating> findAllByAuthorId(@Param("authorId") Long authorId);
}
