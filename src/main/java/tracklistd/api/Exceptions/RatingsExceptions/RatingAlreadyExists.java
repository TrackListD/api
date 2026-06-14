package tracklistd.api.Exceptions.RatingsExceptions;

import tracklistd.api.Entity.Media;

public class RatingAlreadyExists extends RatingException {
    public RatingAlreadyExists(Media media) {
        super("Uma avaliação para esse(a) " + media  + ": " + media.getTitle() + " já existe");
    }
}
