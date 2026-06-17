package tracklistd.api.Service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Repository.MusicRepository;
import tracklistd.api.Repository.RatingRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
class RatingServiceIntegrationTest {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createRating_whenSaved_shouldBePersistedAndRetrievedByAuthorAndTarget() {
        // Arrange
        User author = new User("John Doe", "john_unique_login", Role.MEMBER, Privacy.PUBLIC);
        entityManager.persist(author);

        Music target = new Music();
        ReflectionTestUtils.setField(target, "spotifyID", "spotify_id_integration_test");
        target.setTitle("Test Title Integration");
        musicRepository.save(target);

        entityManager.flush();

        Float ratingNote = 4.5f;
        String review = "Amazing track!";
        Privacy privacy = Privacy.PUBLIC;

        // Act
        Rating createdRating = ratingService.createRating(author, target, ratingNote, review, privacy);
        assertNotNull(createdRating);
        assertNotNull(createdRating.getId());

        entityManager.flush();
        entityManager.clear(); // Clear context to force fetching from database

        // Assert
        Optional<Rating> foundRatingOpt = ratingRepository.findRatingByAuthorAndTarget(author, target);
        assertTrue(foundRatingOpt.isPresent());
        Rating foundRating = foundRatingOpt.get();

        assertEquals(createdRating.getId(), foundRating.getId());
        assertEquals(author.getId(), foundRating.getAuthorPublication().getId());
        assertEquals(target.getSpotifyID(), foundRating.getTargetMedia().getSpotifyID());
        assertEquals(ratingNote, foundRating.getRatingNote());
        assertEquals(review, foundRating.getReview());
        assertEquals(privacy, foundRating.getWhoCanSee());
    }
}
