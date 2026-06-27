package tracklistd.api.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.RatingsExceptions.InvalidRatingNote;
import tracklistd.api.Exceptions.RatingsExceptions.RatingAlreadyExists;
import tracklistd.api.Exceptions.RatingsExceptions.RatingOwnershipViolation;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Repository.CommentRepository;
import tracklistd.api.Repository.LikeRepository;
import tracklistd.api.Repository.RatingRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final MediaService mediaService;

    //Construtor gerenciado pelo Spring Boot
    //Injeção de Dependência
    public RatingService(RatingRepository ratingRepository,
                         CommentRepository commentRepository,
                         LikeRepository likeRepository,
                         MediaService mediaService)
    {
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.mediaService = mediaService;}

    @Transactional
    public Rating createRating(User author, String targetId, Float ratingNote, String review, Privacy whoCanSee )
    {

        Media target = mediaService.getMediaById(targetId);

        if(checkRatingNote(ratingNote))
            throw new InvalidRatingNote(ratingNote);


        Boolean result = checkIfRatingExits(author, target);
        if(result)
            throw new RatingAlreadyExists(target);


        Rating rating = new Rating(author,target,ratingNote, review, whoCanSee);

        ratingRepository.save(rating);
        return rating;

    }

    @Transactional
    public void editReview(String newMessage, Long ratingId, Long authorId)
    {
       Rating rating = findRatingAndValidateOwner(ratingId, authorId);

        rating.editReview(newMessage);

    }

    @Transactional
    public void editRatingNote(Float newRatingNote, Long ratingId, Long authorId)
    {
        Rating rating = findRatingAndValidateOwner(ratingId, authorId);

        if(checkRatingNote(newRatingNote))
            throw new InvalidRatingNote(newRatingNote);

        rating.editNote(newRatingNote);
    }

    @Transactional
    public void changePrivacy(Privacy newPrivacy, Long ratingId, Long authorId)
    {
        Rating rating = findRatingAndValidateOwner(ratingId,authorId);

        rating.setWhoCanSee(newPrivacy);

    }

    @Transactional
    public void deleteRating(Long ratingId, Long authorId)
    {
        Rating rating = findRatingAndValidateOwner(ratingId, authorId);
        ratingRepository.delete(rating);
    }

    protected User hideRating(Rating rating){
        rating.setStatus(ModerationStatus.OCULT);
        return rating.getAuthorPublication();
    }

    @Transactional(readOnly = true)
    public List<Rating> getRatingsByUserPrivacy(User author, Privacy privacy)
    {
        return this.ratingRepository.findRatingByAuthorAndWhoCanSee(author, privacy);
    }

    @Transactional(readOnly = true)
    public List<Rating> getRatingsByUser(User author)
    {
        return this.ratingRepository.findAllByAuthor(author);
    }


    @Transactional(readOnly = true)
    public Rating getRatingById(Long ratingId)
    {
       Rating rating =  this.ratingRepository.findById(ratingId).orElseThrow(
               () -> new ResourceNotFoundException("Essa Avaliação não Existe")
       );

       return rating;
    }

    @Transactional(readOnly = true)
    public Integer getRatingComments(Rating rating)
    {
       return this.commentRepository.countByPost(rating);
    }

    @Transactional(readOnly = true)
    public Long getRatingLikes(Rating rating)
    {
        return this.likeRepository.countByPublicationId(rating.getId());
    }

    @Transactional(readOnly = true)
    public Media getRatingTargetMedia(Rating rating)
    {
        // Como usamos JOIN FETCH no repositório,
        // a mídia já está hidratada na memória. Não precisamos chamar o MediaService.
        return rating.getTargetMedia();
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
                () -> new ResourceNotFoundException("Está Avaliação não existe"));

        if(!Objects.equals(rating.getAuthorPublication().getId(), authorId))
            throw new RatingOwnershipViolation();

        return rating;
    }

}
