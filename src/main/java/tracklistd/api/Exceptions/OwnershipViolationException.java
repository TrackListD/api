package tracklistd.api.Exceptions;

public class OwnershipViolationException extends RuntimeException {
    public OwnershipViolationException() {
        super("Sem permissão");
    }

    public OwnershipViolationException(String message) {
        super(message);
    }
}
