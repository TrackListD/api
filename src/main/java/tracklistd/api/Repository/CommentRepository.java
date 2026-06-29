package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.likes WHERE c.post = :post")
    List<Comment> findAllByPost(@Param("post") Publication post);

    Integer countByPost(Publication post);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.likes WHERE c.author = :author")
    List<Comment> getCommentsByAuthor(@Param("author") User author);
}