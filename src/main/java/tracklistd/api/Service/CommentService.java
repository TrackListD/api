package tracklistd.api.Service;

import org.springframework.stereotype.Service;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.CommentExceptions.CommentException;
import tracklistd.api.Exceptions.CommentExceptions.CommentOwershipViolation;
import tracklistd.api.Exceptions.CommentExceptions.CommentTextBlankException;
import tracklistd.api.Repository.CommentRepository;
import tracklistd.api.Repository.LikeRepository;

import java.util.List;
import java.util.Objects;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public CommentService(CommentRepository commentRepository, LikeRepository likeRepository)
    {
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    public Comment createComment(User author, Publication post, String text)
    {
        if(text.isBlank())
            throw new CommentTextBlankException();
        if(!checkTypeOfPost(post))
            throw new CommentException("Impossivel Comentar um comentario");
        if(Objects.equals(post.getAuthorPublication().getId(), author.getId()))
            throw new CommentException("Impossivel comentar no proprio Post");

        Comment comment = new Comment(author,post,text);
        this.commentRepository.save(comment);

        return comment;
    }

    public void editCommentText(String newText, Long commentId, Long authorId)
    {
        Comment comment = findCommentAndValidateOwner(commentId,authorId);

        if(newText.isBlank())
            throw new CommentTextBlankException();

        comment.editText(newText);
        this.commentRepository.save(comment);

    }

    public void deleteComment(Long commentId, Long authorId)
    {
        Comment comment = this.findCommentAndValidateOwner(commentId,authorId);

        this.commentRepository.delete(comment);
    }

    public List<Comment> getCommentsByUser(User author)
    {
        return this.commentRepository.getCommentsByAuthor(author);
    }

    public Long getCommentLikes(Rating rating)
    {
        return this.likeRepository.countByPublicationId(rating.getId());
    }

    //Metodos Privados
    private Boolean checkTypeOfPost(Publication post)
    {
        if(post instanceof Comment)
            return false;
        return true;
    }

    private Comment findCommentAndValidateOwner(Long commentId, Long authorId)
    {
        Comment comment = this.commentRepository.findById(commentId).orElseThrow(
                () -> new CommentException("Esse comentario não existe")
        );

        if(!Objects.equals(comment.getAuthorPublication().getId(), authorId))
            throw new CommentOwershipViolation();

        return comment;
    }
}
