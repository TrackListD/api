package tracklistd.api.Service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Repository.MusicRepository;
import tracklistd.api.Repository.PublicationRepository;
import tracklistd.api.Repository.UserRepository;

@SpringBootTest
public class FeedServiceTest {
    @Autowired
    FeedService feedService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PublicationRepository publicationRepository;

    @Autowired
    MusicRepository musicRepository;

    @Test
    void testFeed() {
        // cria usuários
        User joao = new User();
        joao.setName("João");
        joao.setIdLoginApi("1234567-" + System.currentTimeMillis());

        joao.setRole(Role.MEMBER);
        joao.setWhoCanComment(Privacy.PUBLIC);

        User maria = new User();
        maria.setName("Maria");
        maria.setIdLoginApi("456789-" + System.currentTimeMillis());
        maria.setRole(Role.MEMBER);
        maria.setWhoCanComment(Privacy.PUBLIC);

        userRepository.save(joao);
        userRepository.save(maria);

        // João segue Maria
        joao.getFollowing().add(maria);
        userRepository.save(joao);

        // cria publicação da Maria
        Rating rating = new Rating();

        rating.setAuthor(maria);

        Music media = new Music();
        media.setSpotifyID("123");
        media.setTitle("Filme teste");

        musicRepository.save(media);

        rating.setMediaTarget(media);

        rating.editNote(5f);
        rating.editReview("Muito bom");

        rating.setWhoCanSee(Privacy.PUBLIC);
        rating.setStatus(ModerationStatus.ACTIVE);

        publicationRepository.save(rating);

        List<Publication> feed = feedService.getSocialFeed(1L);

        assertNotNull(feed);
    }
}
