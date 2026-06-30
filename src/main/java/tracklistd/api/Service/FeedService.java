package tracklistd.api.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tracklistd.api.Dto.Feed.PublicationFeedDTO;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Exceptions.UserExceptions.UserDoesNotExist;
import tracklistd.api.Mapper.FeedMapper;
import tracklistd.api.Repository.PublicationRepository;
import tracklistd.api.Repository.UserRepository;

@Service
public class FeedService {

        private final PublicationRepository publicationRepository;
        private final UserRepository userRepository;
        private final FeedMapper feedMapper;
        private final UserService userService;

        public FeedService(PublicationRepository publicationRepository, UserRepository userRepository,
                        FeedMapper feedMapper, UserService userService) {
                this.publicationRepository = publicationRepository;
                this.userRepository = userRepository;
                this.feedMapper = feedMapper;
                this.userService = userService;
        }

        @Transactional
        public List<PublicationFeedDTO> getSocialFeed(Long userId) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserDoesNotExist(userId));

                List<Privacy> allowedPrivacies = List.of(Privacy.PUBLIC, Privacy.JUST_FOLLOWERS);

                List<Publication> publications = publicationRepository
                                .findByAuthorInAndWhoCanSeeInOrderByPublicationDateDesc(user.getFollowing(), allowedPrivacies);

                return publications.stream()
                                .map(publication -> feedMapper.toFeedDTO(publication, userId))
                                .toList();
        }

        @Transactional
        public List<PublicationFeedDTO> getGlobalFeed(Long userId) {
                LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

                List<Publication> trending = publicationRepository.findTrending(oneWeekAgo);

                return trending.stream().map(publication -> feedMapper.toFeedDTO(publication, userId))
                                .toList();

        }

        @Transactional
        public List<PublicationFeedDTO> getUserFeed(Long userId, Long myUserId) {
                List<Privacy> allowedPrivacies;
                if (userId.equals(myUserId)) {
                        allowedPrivacies = List.of(Privacy.PUBLIC, Privacy.JUST_FOLLOWERS, Privacy.PRIVATE);
                } else if (userService.isFollowing(myUserId, userId)) {
                        allowedPrivacies = List.of(Privacy.PUBLIC, Privacy.JUST_FOLLOWERS);
                } else {
                        allowedPrivacies = List.of(Privacy.PUBLIC);
                }

                List<Publication> posts = publicationRepository.findByAuthorIdAndWhoCanSeeInOrderByPublicationDateDesc(userId, allowedPrivacies);
                return posts.stream()
                                .map(post -> feedMapper.toFeedDTO(post, myUserId))
                                .toList();

        }
}
