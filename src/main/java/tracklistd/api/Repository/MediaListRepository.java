package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaListRepository extends JpaRepository<MediaList, Long> {

    // Métodos CRUD básicos já vêm inclusos (save, findById, findAll, delete, etc.)

    //Método que retorna todas as Listas de um User
    List<MediaList> findAllByAuthor(User author);

    //Método que retorna todas as Listas de um User a depender da Privacidade
    List<MediaList> findAllByAuthorAndWhoCanSee(User author, Privacy whoCanSee);

    //Método que retorna todas as Listas favoritas de um User
    List<MediaList> findAllByAuthorAndIsFavorite(User author, Boolean isFavorite);

    //Método que retorna uma lista com um nome, para verificar Unicidade
    Optional<MediaList> findMediaListByAuthorAndListName(User author, String listName);
}
