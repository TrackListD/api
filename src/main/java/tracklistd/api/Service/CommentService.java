package tracklistd.api.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.CommentExceptions.*;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Repository.CommentRepository;
import tracklistd.api.Repository.LikeRepository;

import java.util.List;
import java.util.Objects;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public CommentService(CommentRepository commentRepository, LikeRepository likeRepository) {
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    @Transactional
    public Comment createComment(User author, Publication post, String text) {
        if (text.isBlank())
            throw new CommentTextBlankException();
        if (!checkTypeOfPost(post))
            throw new CommentOnCommentException();
        if (Objects.equals(post.getAuthorPublication().getId(), author.getId()))
            throw new SelfCommentException();

        Comment comment = new Comment(author, post, text);
        this.commentRepository.save(comment);

        return comment;
    }

    @Transactional
    public void editCommentText(String newText, Long commentId, Long authorId) {
        Comment comment = findCommentAndValidateOwner(commentId, authorId);

        if (newText.isBlank())
            throw new CommentTextBlankException();

        comment.editText(newText);
    }

    @Transactional
    public void deleteComment(Long commentId, Long authorId) {
        Comment comment = this.findCommentAndValidateOwner(commentId, authorId);

        this.commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPost(Publication post) {

        return this.commentRepository.findAllByPost(post);
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(Long commentId) {

        return this.commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Esse comentario não existe"));
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByUser(User author) {

        return this.commentRepository.getCommentsByAuthor(author);
    }

    @Transactional(readOnly = true)
    public Long getCommentLikes(Comment comment) {
        return this.likeRepository.countByPublicationId(comment.getId());
    }

    // Metodos Privados
    private Boolean checkTypeOfPost(Publication post) {
        if (post instanceof Comment)
            return false;
        return true;
    }

    private Comment findCommentAndValidateOwner(Long commentId, Long authorId) {
        Comment comment = this.commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Esse comentario não existe"));

        if (!Objects.equals(comment.getAuthorPublication().getId(), authorId))
            throw new CommentOwershipViolation();

        return comment;
    }

    @Transactional
    public User hideComment(Comment comment) {
        comment.setModerationStatus(ModerationStatus.OCULT);
        commentRepository.save(comment);
        return comment.getAuthorPublication();
    }

    public boolean hasUserLikedComment(Long commentId, Long userId) {
        return this.likeRepository.existsByUserIdAndPublicationId(userId, commentId);

    }
}