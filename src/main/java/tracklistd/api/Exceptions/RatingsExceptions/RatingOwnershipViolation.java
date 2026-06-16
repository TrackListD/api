package tracklistd.api.Exceptions.RatingsExceptions;

public class RatingOwnershipViolation extends RatingException  {
    public RatingOwnershipViolation() {
        super("Você está tentando modificar uma Avaliação que não é sua");
    }
}
