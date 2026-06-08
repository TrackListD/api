package tracklistd.api.Exceptions.CommentExceptions;

public class CommentOwershipViolation extends CommentException {
    public CommentOwershipViolation() {
        super("Esse comentario não te pertence, impossivel Editar");
    }
}
