package tracklistd.api.Exceptions.CommentExceptions;

public class CommentOnCommentException extends RuntimeException {
    public CommentOnCommentException() {
        super("Impossivel Comentar um comentario");
    }
}
