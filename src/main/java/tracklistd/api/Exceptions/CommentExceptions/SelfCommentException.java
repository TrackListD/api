package tracklistd.api.Exceptions.CommentExceptions;

public class SelfCommentException extends RuntimeException {
    public SelfCommentException() {
        super("Impossivel comentar no proprio Post");
    }
}
