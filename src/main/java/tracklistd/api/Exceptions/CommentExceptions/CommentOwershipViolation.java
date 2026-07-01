package tracklistd.api.Exceptions.CommentExceptions;

import tracklistd.api.Exceptions.OwnershipViolationException;

public class CommentOwershipViolation extends OwnershipViolationException {
    public CommentOwershipViolation() {
        super("Esse comentario não te pertence, impossivel Editar");
    }
}
