package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Métodos CRUD básicos já vêm inclusos (save, findById, findAll, delete, etc.)

    //Método que retorna todas os comentarios feitos em um Publicação de um User
    List<Comment> findAllByPost(Publication post);

    //Método que retorna a qtd de comentarios de um post
    Integer countByPost(Publication post);

    //Método que retorna os comentarios de um User
    List<Comment> getCommentsByAuthor(User author);
}

