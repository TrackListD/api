package tracklistd.api.Dto.Feed;

import java.time.LocalDateTime;

import tracklistd.api.Dto.User.UserMinResponseDTO;

public record PublicationFeedDTO(
                Long id,
                String content,
                String type, // "RATING", "MEDIA_LIST", "COMMENT"
                Float rating,
                LocalDateTime publicationDate,
                UserMinResponseDTO author,
                Long likesCount,
                boolean likedByMe) {
}