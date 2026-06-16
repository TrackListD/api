package tracklistd.api.Exceptions.CommentExceptions;

public class CommentTextBlankException extends CommentException {
    public CommentTextBlankException() {
        super("O texto do comentario não pode estar em Branco");
    }
}
