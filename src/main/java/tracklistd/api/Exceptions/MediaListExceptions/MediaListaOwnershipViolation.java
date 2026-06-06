package tracklistd.api.Exceptions.MediaListExceptions;

public class MediaListaOwnershipViolation extends RuntimeException {
    public MediaListaOwnershipViolation() {
        super("Você está tentando modificar uma Avaliação que não é sua");
    }
}
