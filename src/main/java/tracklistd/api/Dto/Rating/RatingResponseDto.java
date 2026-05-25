package tracklistd.api.Dto.Rating;

import java.time.LocalDateTime;

public record RatingResponseDto(
        Long authorId,
        Long targetId,
        LocalDateTime publicationDate,
        Float ratingNote,
        String review,
        String authorName,
        String targetName,
        Integer likeCount,
        Integer commentCount
) { }

