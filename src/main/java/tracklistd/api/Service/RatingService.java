package tracklistd.api.Service;

import org.springframework.stereotype.Service;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.RatingsExceptions.InvalidRatingNote;
import tracklistd.api.Exceptions.RatingsExceptions.RatingAlreadyExists;
import tracklistd.api.Exceptions.RatingsExceptions.RatingException;
import tracklistd.api.Exceptions.RatingsExceptions.RatingOwnershipViolation;
import tracklistd.api.Repository.RatingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    //Construtor gerenciado pelo Spring Boot
    //Injeção de Dependência
    public RatingService(RatingRepository ratingRepository)
    {
        this.ratingRepository = ratingRepository;
    }

    public Rating createRating(User author, Media target, Float ratingNote, String review, Privacy whoCanSee )
    {
        if(checkRatingNote(ratingNote))
            throw new InvalidRatingNote(ratingNote);


        Boolean result = checkIfRatingExits(author, target);
        if(result)
            throw new RatingAlreadyExists(target);


        Rating rating = new Rating(author,target,ratingNote, review, whoCanSee);

        ratingRepository.save(rating);
        return rating;

    }

    public void editReview(String newMessage, Long ratingId, Long authorId)
    {
       Rating rating = findRatingAndValidateOwner(ratingId, authorId);

        rating.editReview(newMessage);
        ratingRepository.save(rating);

    }

    public void editRatingNote(Float newRatingNote, Long ratingId, Long authorId)
    {
        Rating rating = findRatingAndValidateOwner(ratingId, authorId);

        if(checkRatingNote(newRatingNote))
            throw new InvalidRatingNote(newRatingNote);

        rating.editNote(newRatingNote);
        ratingRepository.save(rating);
    }

    public void changePrivacy(Privacy newPrivacy, Long ratingId, Long authorId)
    {
        Rating rating = findRatingAndValidateOwner(ratingId,authorId);

        rating.setPrivacy(newPrivacy);
        ratingRepository.save(rating);

    }

    public void deleteRating(Long ratingId, Long authorId)
    {
        Rating rating = findRatingAndValidateOwner(ratingId, authorId);
        ratingRepository.delete(rating);
    }

    public List<Rating> getRatingsByUser(User author)
    {
        return this.ratingRepository.findRatingByAuthorAndWhoCanSee(author, Privacy.PUBLIC);
    }

    // Métodos Privados

    private Boolean checkIfRatingExits(User author, Media target)
    {
        Optional<Rating> result = ratingRepository.findRatingByAuthorAndTarget(author, target);

        return result.isPresent();
    }

    private boolean checkRatingNote(Float ratingNote)
    {
        return (ratingNote < 0 || ratingNote > 5) || (ratingNote % 0.5 != 0);
    }

    private Rating findRatingAndValidateOwner(Long ratingId, Long authorId)
    {
        Rating rating = ratingRepository.findById(ratingId).orElseThrow(
                () -> new RatingException("Está Avaliação não existe"));

        if(!Objects.equals(rating.getAuthorPublication().getId(), authorId))
            throw new RatingOwnershipViolation();

        return rating;
    }

}
