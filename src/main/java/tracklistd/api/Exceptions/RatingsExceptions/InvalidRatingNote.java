package tracklistd.api.Exceptions.RatingsExceptions;

public class InvalidRatingNote extends RatingException  {
    public InvalidRatingNote(Float ratingNote) {
        super("Esta nota: " + ratingNote + " não é valida");
    }
}
