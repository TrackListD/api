package tracklistd.api.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tracklistd.api.Dto.Feed.PublicationFeedDTO;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;
import tracklistd.api.Mapper.FeedMapper;
import tracklistd.api.Repository.PublicationRepository;
import tracklistd.api.Repository.UserRepository;

@Service
public class FeedService {

        private final PublicationRepository publicationRepository;
        private final UserRepository userRepository;
        private final FeedMapper feedMapper;

        public FeedService(PublicationRepository publicationRepository, UserRepository userRepository,
                        FeedMapper feedMapper) {
                this.publicationRepository = publicationRepository;
                this.userRepository = userRepository;
                this.feedMapper = feedMapper;
        }

        @Transactional
        public List<PublicationFeedDTO> getSocialFeed(Long userId) {

                User user = userRepository.findById(userId)
                                .orElseThrow();

                List<Publication> publications = publicationRepository
                                .findByAuthorInOrderByPublicationDateDesc(user.getFollowing());

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
}
