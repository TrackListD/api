package tracklistd.api.Exceptions.MediaListExceptions;

import tracklistd.api.Exceptions.OwnershipViolationException;

public class MediaListaOwnershipViolation extends OwnershipViolationException {
    public MediaListaOwnershipViolation() {
        super("Você está tentando modificar uma Avaliação que não é sua");
    }
}
