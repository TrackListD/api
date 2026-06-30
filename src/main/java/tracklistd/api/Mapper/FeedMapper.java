package tracklistd.api.Mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import tracklistd.api.Dto.Feed.PublicationFeedDTO;
import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Dto.MediaList.MediaListResponseDto;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.UserExceptions.UserDoesNotExist;
import tracklistd.api.Repository.LikeRepository;
import tracklistd.api.Repository.UserRepository;

@Component
public class FeedMapper {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final UserRepository userRepository;
    private final MediaMapper mediaMapper;

    public FeedMapper(LikeRepository likeRepository, LikeMapper likeMapper, MediaMapper mediaMapper,
            UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.likeMapper = likeMapper;
        this.mediaMapper = mediaMapper;
        this.userRepository = userRepository;
    }

    public PublicationFeedDTO toFeedDTO(Publication publication, Long currentUserId, Long likesCount, Integer commentsCount) {
        boolean likedByMe = currentUserId != null &&
                likeRepository.existsByUserIdAndPublicationId(currentUserId, publication.getId());

        String content = switch (publication) {
            case Rating r -> r.getReview();
            case MediaList m -> m.getListName() + " (" + m.getTypeOfList().toString() + ")";
            default -> "";
        };

        String type = switch (publication) {
            case Rating r -> "RATING";
            case MediaList m -> "MEDIA_LIST";
            default -> "UNKNOWN";
        };

        Float ratingValue = switch (publication) {
            case Rating r -> r.getRatingNote();
            default -> null;
        };

        MediaMinDTO mediaDTO = switch (publication) {
            case Rating r -> r.getTargetMedia() != null ? mediaMapper.toMinDTO(r.getTargetMedia()) : null;
            default -> null;
        };

        List<MediaMinDTO> mediaListItems = switch (publication) {
            case MediaList m -> m.getMedia()
                    .stream()
                    .map(mediaMapper::toMinDTO)
                    .toList();
            default -> null;
        };

        boolean authorFollowedByAuthUser = false;

        if (currentUserId != null) {
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new UserDoesNotExist(currentUserId));
            if (currentUser != null) {
                authorFollowedByAuthUser = currentUser.getFollowing()
                        .stream()
                        .anyMatch(u -> u.getId().equals(publication.getAuthor().getId()));
            }
        }

        return new PublicationFeedDTO(
                publication.getId(),
                content,
                type,
                ratingValue,
                publication.getPublicationDate(),
                likeMapper.toUserMinDTO(publication.getAuthor()),
                likesCount,
                commentsCount,
                likedByMe,
                mediaDTO,
                authorFollowedByAuthUser,
                mediaListItems);
    }
}