package tracklistd.api.Mapper;

import org.springframework.stereotype.Component;

import tracklistd.api.Dto.Feed.PublicationFeedDTO;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Repository.LikeRepository;

@Component
public class FeedMapper {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;

    public FeedMapper(LikeRepository likeRepository, LikeMapper likeMapper) {
        this.likeRepository = likeRepository;
        this.likeMapper = likeMapper;
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

        return new PublicationFeedDTO(
                publication.getId(),
                content,
                type,
                ratingValue,
                publication.getPublicationDate(),
                likeMapper.toUserMinDTO(publication.getAuthor()),
                likesCount,
                likedByMe);
    }
}