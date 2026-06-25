package tracklistd.api.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tracklistd.api.Dto.Like.LikeResponseDTO;
import tracklistd.api.Dto.User.UserMinResponseDTO;
import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Exceptions.PublicationExceptions.PublicationDoesNotExist;
import tracklistd.api.Repository.MediaRepository;
import tracklistd.api.Repository.RatingRepository;
import tracklistd.api.Repository.UserRepository;

@SpringBootTest(properties = {
                "SPOTIFY_CLIENT_ID=test-client-id",
                "SPOTIFY_CLIENT_SECRET=test-client-secret"
})
class LikeServiceTest {

        @Autowired
        private LikeService likeService;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RatingRepository ratingRepository;

        @Autowired
        private MediaRepository mediaRepository;

        @Test
        void shouldLikePublication() {
                User user = userRepository.save(
                                new User(
                                                "João Pedro",
                                                "auth0|" + System.currentTimeMillis(),
                                                Role.MEMBER,
                                                Privacy.PUBLIC));

                Music music = new Music();
                music.setSpotifyID("spotify-" + System.nanoTime());
                music.setTitle("Numb");
                music.setReleaseDate(LocalDate.of(2003, 3, 25));
                music.setDuration(185);

                mediaRepository.save(music);

                Rating rating = ratingRepository.save(
                                new Rating(
                                                user,
                                                music,
                                                4.5f,
                                                "Excelente álbum",
                                                Privacy.PUBLIC));

                LikeResponseDTO response = likeService.toggleLike(user, rating.getId());

                assertTrue(response.liked());
                assertEquals(1L, response.likesCount());
        }

        @Test
        void shouldUnlikePublication() {
                User user = userRepository.save(
                                new User(
                                                "João Pedro",
                                                "auth0|" + System.currentTimeMillis(),
                                                Role.MEMBER,
                                                Privacy.PUBLIC));

                Music music = new Music();
                music.setSpotifyID("spotify-" + System.nanoTime());
                music.setTitle("Numb");
                music.setReleaseDate(LocalDate.of(2003, 3, 25));
                music.setDuration(185);

                mediaRepository.save(music);

                Rating rating = ratingRepository.save(
                                new Rating(
                                                user,
                                                music,
                                                4.5f,
                                                "Excelente álbum",
                                                Privacy.PUBLIC));

                likeService.toggleLike(user, rating.getId());

                LikeResponseDTO response = likeService.toggleLike(user, rating.getId());

                assertFalse(response.liked());
                assertEquals(0L, response.likesCount());
        }

        @Test
        void shouldToggleLike() {
                User user = userRepository.save(
                                new User(
                                                "João Pedro",
                                                "auth0|" + System.currentTimeMillis(),
                                                Role.MEMBER,
                                                Privacy.PUBLIC));

                Music music = new Music();
                music.setSpotifyID("spotify-" + System.nanoTime());
                music.setTitle("Numb");
                music.setReleaseDate(LocalDate.of(2003, 3, 25));
                music.setDuration(185);

                mediaRepository.save(music);

                Rating rating = ratingRepository.save(
                                new Rating(
                                                user,
                                                music,
                                                4.5f,
                                                "Excelente álbum",
                                                Privacy.PUBLIC));

                LikeResponseDTO first = likeService.toggleLike(user, rating.getId());

                assertTrue(first.liked());
                assertEquals(1L, first.likesCount());

                LikeResponseDTO second = likeService.toggleLike(user, rating.getId());

                assertFalse(second.liked());
                assertEquals(0L, second.likesCount());
        }

        @Test
        void shouldCountLikesFromDifferentUsers() {
                User user1 = userRepository.save(
                                new User(
                                                "User 1",
                                                "auth0|" + System.currentTimeMillis(),
                                                Role.MEMBER,
                                                Privacy.PUBLIC));

                User user2 = userRepository.save(
                                new User(
                                                "User 2",
                                                "auth0|" + System.nanoTime(),
                                                Role.MEMBER,
                                                Privacy.PUBLIC));

                Music music = new Music();
                music.setSpotifyID("spotify-" + System.nanoTime());
                music.setTitle("Numb");
                music.setReleaseDate(LocalDate.of(2003, 3, 25));
                music.setDuration(185);

                mediaRepository.save(music);

                Rating rating = ratingRepository.save(
                                new Rating(
                                                user1,
                                                music,
                                                4.5f,
                                                "Excelente álbum",
                                                Privacy.PUBLIC));

                likeService.toggleLike(user1, rating.getId());

                LikeResponseDTO response = likeService.toggleLike(user2, rating.getId());

                assertTrue(response.liked());
                assertEquals(2L, response.likesCount());
        }

        @Test
        void shouldThrowExceptionWhenPublicationDoesNotExist() {
                User user = userRepository.save(
                                new User(
                                                "João Pedro",
                                                "auth0|" + System.currentTimeMillis(),
                                                Role.MEMBER,
                                                Privacy.PUBLIC));

                // Como o seu código lança .orElseThrow() sem parâmetro em toggleLike, ele solta
                // NoSuchElementException
                assertThrows(
                                java.util.NoSuchElementException.class,
                                () -> likeService.toggleLike(user, 999999L));
        }

        @Test
        void shouldGetUsersWhoLikedPublication() {
                User user1 = userRepository.save(
                                new User(
                                                "User 1",
                                                "auth0|" + System.currentTimeMillis(),
                                                Role.MEMBER,
                                                Privacy.PUBLIC));

                User user2 = userRepository.save(
                                new User(
                                                "User 2",
                                                "auth0|" + System.nanoTime(),
                                                Role.MEMBER,
                                                Privacy.PUBLIC));

                Music music = new Music();
                music.setSpotifyID("spotify-" + System.nanoTime());
                music.setTitle("Numb");
                music.setReleaseDate(LocalDate.of(2003, 3, 25));
                music.setDuration(185);

                mediaRepository.save(music);

                Rating rating = ratingRepository.save(
                                new Rating(
                                                user1,
                                                music,
                                                4.5f,
                                                "Excelente álbum",
                                                Privacy.PUBLIC));

                // Adiciona curtidas
                likeService.toggleLike(user1, rating.getId());
                likeService.toggleLike(user2, rating.getId());

                // Busca quem curtiu
                List<UserMinResponseDTO> usersWhoLiked = likeService.getWhoLiked(rating.getId());

                assertNotNull(usersWhoLiked);
                assertEquals(2, usersWhoLiked.size());
        }

        @Test
        void shouldThrowPublicationDoesNotExistWhenGettingLikesFromInvalidPublication() {
                assertThrows(
                                PublicationDoesNotExist.class,
                                () -> likeService.getWhoLiked(999999L));
        }
}