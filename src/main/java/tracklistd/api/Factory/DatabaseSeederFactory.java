package tracklistd.api.Factory;

import org.springframework.stereotype.Component;

import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Repository.MusicRepository;
import tracklistd.api.Repository.PublicationRepository;
import tracklistd.api.Repository.UserRepository;

@Component
public class DatabaseSeederFactory {
    private final UserRepository userRepository;
    private final MusicRepository musicRepository;
    private final PublicationRepository publicationRepository;

    public DatabaseSeederFactory(UserRepository userRepository,
            MusicRepository musicRepository,
            PublicationRepository publicationRepository) {
        this.userRepository = userRepository;
        this.musicRepository = musicRepository;
        this.publicationRepository = publicationRepository;
    }

    public User createAndSaveUser(String name, Role role) {
        User user = new User();
        user.setName(name);
        user.setIdLoginApi("login-" + name.toLowerCase() + "-" + System.currentTimeMillis());
        user.setRole(role);
        user.setWhoCanComment(Privacy.PUBLIC);
        return userRepository.save(user);
    }

    public Music createAndSaveMusic(String title, String spotifyId) {
        Music music = new Music();
        music.setTitle(title);
        music.setSpotifyID(spotifyId);
        return musicRepository.save(music);
    }

    public Rating createAndSaveRating(User author, Music music, float note, String review) {
        Rating rating = new Rating();
        rating.setAuthor(author);
        rating.setMediaTarget(music);
        rating.editNote(note);
        rating.editReview(review);
        rating.setWhoCanSee(Privacy.PUBLIC);
        rating.setStatus(ModerationStatus.ACTIVE);
        return publicationRepository.save(rating);
    }
}
