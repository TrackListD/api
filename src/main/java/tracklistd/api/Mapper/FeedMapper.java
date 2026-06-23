package tracklistd.api.Mapper;

import org.springframework.stereotype.Component;

import tracklistd.api.Dto.Feed.PublicationFeedDTO;
import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Repository.LikeRepository;

@Component
public class FeedMapper {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final MediaMapper mediaMapper;

    public FeedMapper(LikeRepository likeRepository, LikeMapper likeMapper, MediaMapper mediaMapper) {
        this.likeRepository = likeRepository;
        this.likeMapper = likeMapper;
        this.mediaMapper = mediaMapper;
    }

    public PublicationFeedDTO toFeedDTO(Publication publication, Long currentUserId) {
        long likesCount = likeRepository.countByPublicationId(publication.getId());
        boolean likedByMe = currentUserId != null &&
                likeRepository.existsByUserIdAndPublicationId(currentUserId, publication.getId());

        String content = switch (publication) {
            case Rating r -> r.getReview();
            case MediaList m -> m.getTypeOfList().toString();
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

        return new PublicationFeedDTO(
                publication.getId(),
                content,
                type,
                ratingValue,
                publication.getPublicationDate(),
                likeMapper.toUserMinDTO(publication.getAuthor()),
                likesCount,
                likedByMe,
                mediaDTO);
    }
}