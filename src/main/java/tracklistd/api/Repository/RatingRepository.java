package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
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

    //Método que retornar todas s Avaliações de um Usuario
    List<Rating> findAllByAuthor(User author);

    //Método que busca todas as Avaliações Publicas de um Usuario
    List<Rating> findRatingByAuthorAndWhoCanSee(User author,Privacy whoCanSee);
}
