package tracklistd.api.Dto.Feed;

import java.time.LocalDateTime;
import java.util.List;

import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Dto.MediaList.MediaListResponseDto;
import tracklistd.api.Dto.User.UserMinResponseDTO;

public record PublicationFeedDTO(
        Long id,
        String content,
        String type, // "RATING", "MEDIA_LIST", "COMMENT"
        Float rating,
        LocalDateTime publicationDate,
        UserMinResponseDTO author,
        Long likesCount,
        Integer commentsCount,
        boolean likedByMe,
        MediaMinDTO media,
        boolean authorFollowedByAuthUser,
        List<MediaMinDTO> mediaList) {
}