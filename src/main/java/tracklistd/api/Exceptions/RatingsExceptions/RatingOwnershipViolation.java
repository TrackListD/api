package tracklistd.api.Exceptions.RatingsExceptions;

import tracklistd.api.Exceptions.OwnershipViolationException;

public class RatingOwnershipViolation extends OwnershipViolationException  {
    public RatingOwnershipViolation() {
        super("Você está tentando modificar uma Avaliação que não é sua");
    }
}
