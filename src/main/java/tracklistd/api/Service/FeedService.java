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
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Interfaces.Reportable;
import tracklistd.api.Exceptions.UserExceptions.UserDoesNotExist;
import tracklistd.api.Mapper.FeedMapper;
import tracklistd.api.Repository.PublicationRepository;
import tracklistd.api.Repository.UserRepository;
import tracklistd.api.Repository.LikeRepository;
import tracklistd.api.Repository.CommentRepository;

@Service
public class FeedService {

        private final PublicationRepository publicationRepository;
        private final UserRepository userRepository;
        private final FeedMapper feedMapper;
        private final UserService userService;
        private final LikeRepository likeRepository;
        private final CommentRepository commentRepository;

        public FeedService(PublicationRepository publicationRepository, UserRepository userRepository,
                        FeedMapper feedMapper, UserService userService, LikeRepository likeRepository,
                        CommentRepository commentRepository) {
                this.publicationRepository = publicationRepository;
                this.userRepository = userRepository;
                this.feedMapper = feedMapper;
                this.userService = userService;
                this.likeRepository = likeRepository;
                this.commentRepository = commentRepository;
        }

        @Transactional
        public List<PublicationFeedDTO> getSocialFeed(Long userId) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserDoesNotExist(userId));

                List<Privacy> allowedPrivacies = List.of(Privacy.PUBLIC, Privacy.JUST_FOLLOWERS);

                List<Publication> publications = publicationRepository
                                .findSocialFeed(user.getFollowing(), allowedPrivacies);

                return publications.stream()
                                .filter(pub -> {
                                    if(pub instanceof Reportable reportablePub){
                                        return reportablePub.getStatusModeration() != ModerationStatus.OCULT;
                                    }
                                    return true;
                                })
                                .map(publication -> {
                                        long likesCount = likeRepository.countByPublicationId(publication.getId());
                                        int commentsCount = commentRepository.countByPost(publication);
                                        return feedMapper.toFeedDTO(publication, userId, likesCount, commentsCount);
                                })
                                .toList();
        }

        @Transactional
        public List<PublicationFeedDTO> getGlobalFeed(Long userId) {
                LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

                List<Publication> trending = publicationRepository.findTrending(oneWeekAgo);

                return trending.stream()
                                .filter(pub -> {
                                    if(pub instanceof Reportable reportablePub){
                                        return reportablePub.getStatusModeration() != ModerationStatus.OCULT;
                                    }
                                    return true;
                                })
                                .map(publication -> {
                                        long likesCount = likeRepository.countByPublicationId(publication.getId());
                                        int commentsCount = commentRepository.countByPost(publication);
                                        return feedMapper.toFeedDTO(publication, userId, likesCount, commentsCount);
                                })
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

                List<Publication> posts = publicationRepository.findUserFeed(userId, allowedPrivacies);
                return posts.stream()
                                .filter(pub -> {
                                    if(pub instanceof Reportable reportablePub){
                                        return reportablePub.getStatusModeration() != ModerationStatus.OCULT;
                                    }
                                    return true;
                                })
                                .map(post -> {
                                        long likesCount = likeRepository.countByPublicationId(post.getId());
                                        int commentsCount = commentRepository.countByPost(post);
                                        return feedMapper.toFeedDTO(post, myUserId, likesCount, commentsCount);
                                })
                                .toList();

        }
}
