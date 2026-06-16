package tracklistd.api.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.RatingsExceptions.InvalidRatingNote;
import tracklistd.api.Exceptions.RatingsExceptions.RatingAlreadyExists;
import tracklistd.api.Exceptions.RatingsExceptions.RatingException;
import tracklistd.api.Exceptions.RatingsExceptions.RatingOwnershipViolation;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Repository.RatingRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    private User author;
    private Music target;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);

        target = new Music();
        target.setTitle("Test Song");
    }

    // --- createRating Tests ---

    @Test
    void createRating_whenNoteIsInvalid_shouldThrowInvalidRatingNote() {
        // Arrange
        Float invalidNote = 5.2f;

        // Act & Assert
        assertThrows(InvalidRatingNote.class,
                () -> ratingService.createRating(author, target, invalidNote, "Great song", Privacy.PUBLIC));
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void createRating_whenRatingAlreadyExists_shouldThrowRatingAlreadyExists() {
        // Arrange
        Float validNote = 4.5f;
        Rating existingRating = new Rating(author, target, validNote, "Existing review", Privacy.PUBLIC);
        when(ratingRepository.findRatingByAuthorAndTarget(author, target)).thenReturn(Optional.of(existingRating));

        // Act & Assert
        assertThrows(RatingAlreadyExists.class,
                () -> ratingService.createRating(author, target, validNote, "New review", Privacy.PUBLIC));
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void createRating_whenSuccess_shouldReturnRatingAndCallSave() {
        // Arrange
        Float validNote = 4.5f;
        String review = "Amazing track!";
        Privacy privacy = Privacy.PUBLIC;
        when(ratingRepository.findRatingByAuthorAndTarget(author, target)).thenReturn(Optional.empty());

        // Act
        Rating result = ratingService.createRating(author, target, validNote, review, privacy);

        // Assert
        assertNotNull(result);
        assertEquals(author, result.getAuthorPublication());
        assertEquals(target, result.getTargetMedia());
        assertEquals(validNote, result.getRatingNote());
        assertEquals(review, result.getReview());
        assertEquals(privacy, result.getWhoCanSee());

        verify(ratingRepository, times(1)).save(result);
    }

    // --- editRatingNote Tests ---

    @Test
    void editRatingNote_whenRatingDoesNotExist_shouldThrowRatingException() {
        // Arrange
        Long ratingId = 99L;
        Float newNote = 4.0f;
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.empty());

        // Act & Assert

        // Service
        assertThrows(ResourceNotFoundException.class,
                () -> ratingService.editRatingNote(newNote, ratingId, author.getId()));
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void editRatingNote_whenUserIsNotOwner_shouldThrowRatingOwnershipViolation() {
        // Arrange
        Long ratingId = 10L;
        Float newNote = 4.0f;
        Rating rating = new Rating(author, target, 3.5f, "Review", Privacy.PUBLIC);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        // Act & Assert
        assertThrows(RatingOwnershipViolation.class, () -> ratingService.editRatingNote(newNote, ratingId, 999L) // 999L
                                                                                                                 // is
                                                                                                                 // not
                                                                                                                 // the
                                                                                                                 // author's
                                                                                                                 // ID
                                                                                                                 // (1L)
        );
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void editRatingNote_whenNoteIsInvalid_shouldThrowInvalidRatingNote() {
        // Arrange
        Long ratingId = 10L;
        Float invalidNote = -0.5f;
        Rating rating = new Rating(author, target, 3.5f, "Review", Privacy.PUBLIC);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        // Act & Assert
        assertThrows(InvalidRatingNote.class,
                () -> ratingService.editRatingNote(invalidNote, ratingId, author.getId()));
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void editRatingNote_whenSuccess_shouldCallSave() {
        // Arrange
        Long ratingId = 10L;
        Float newNote = 4.0f;
        Rating rating = new Rating(author, target, 3.5f, "Review", Privacy.PUBLIC);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        // Act
        ratingService.editRatingNote(newNote, ratingId, author.getId());

        // Assert
        assertEquals(newNote, rating.getRatingNote());
        verify(ratingRepository, times(1)).save(rating);
    }

    // --- editReview Tests ---

    @Test
    void editReview_whenRatingDoesNotExist_shouldThrowRatingException() {
        // Arrange
        Long ratingId = 99L;
        String newReview = "New Review Text";
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.empty());

        // Act & Assert

        // Service
        assertThrows(ResourceNotFoundException.class,
                () -> ratingService.editReview(newReview, ratingId, author.getId()));
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void editReview_whenUserIsNotOwner_shouldThrowRatingOwnershipViolation() {
        // Arrange
        Long ratingId = 10L;
        String newReview = "New Review Text";
        Rating rating = new Rating(author, target, 3.5f, "Old Review", Privacy.PUBLIC);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        // Act & Assert
        assertThrows(RatingOwnershipViolation.class, () -> ratingService.editReview(newReview, ratingId, 999L) // 999L
                                                                                                               // is not
                                                                                                               // the
                                                                                                               // author's
                                                                                                               // ID
                                                                                                               // (1L)
        );
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void editReview_whenSuccess_shouldCallSave() {
        // Arrange
        Long ratingId = 10L;
        String newReview = "New Review Text";
        Rating rating = new Rating(author, target, 3.5f, "Old Review", Privacy.PUBLIC);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        // Act
        ratingService.editReview(newReview, ratingId, author.getId());

        // Assert
        assertEquals(newReview, rating.getReview());
        verify(ratingRepository, times(1)).save(rating);
    }

    // --- deleteRating Tests ---

    @Test
    void deleteRating_whenRatingDoesNotExist_shouldThrowRatingException() {
        // Arrange
        Long ratingId = 99L;
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.empty());

        // Act & Assert
        // Service
        assertThrows(ResourceNotFoundException.class, () -> ratingService.deleteRating(ratingId, author.getId()));
        verify(ratingRepository, never()).delete(any(Rating.class));
    }

    @Test
    void deleteRating_whenUserIsNotOwner_shouldThrowRatingOwnershipViolation() {
        // Arrange
        Long ratingId = 10L;
        Rating rating = new Rating(author, target, 3.5f, "Review", Privacy.PUBLIC);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        // Act & Assert
        assertThrows(RatingOwnershipViolation.class, () -> ratingService.deleteRating(ratingId, 999L) // 999L is not the
                                                                                                      // author's ID
                                                                                                      // (1L)
        );
        verify(ratingRepository, never()).delete(any(Rating.class));
    }

    @Test
    void deleteRating_whenSuccess_shouldCallDelete() {
        // Arrange
        Long ratingId = 10L;
        Rating rating = new Rating(author, target, 3.5f, "Review", Privacy.PUBLIC);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        // Act
        ratingService.deleteRating(ratingId, author.getId());

        // Assert
        verify(ratingRepository, times(1)).delete(rating);
    }

    // --- getRatingsByUser Tests ---

    @Test
    void getRatingsByUser_whenSuccess_shouldReturnOnlyPublicRatings() {
        // Arrange
        Rating rating = new Rating(author, target, 4.0f, "Public Review", Privacy.PUBLIC);
        List<Rating> expectedRatings = Collections.singletonList(rating);
        when(ratingRepository.findRatingByAuthorAndWhoCanSee(author, Privacy.PUBLIC)).thenReturn(expectedRatings);

        // Act
        List<Rating> result = ratingService.getRatingsByUser(author);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rating, result.get(0));
        verify(ratingRepository, times(1)).findRatingByAuthorAndWhoCanSee(author, Privacy.PUBLIC);
    }
}