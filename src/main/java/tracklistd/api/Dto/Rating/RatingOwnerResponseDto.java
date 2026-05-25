package tracklistd.api.Dto.Rating;

import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Enums.Privacy;

import java.time.LocalDateTime;

public record RatingOwnerResponseDto(
        RatingResponseDto publicDto,
        LocalDateTime updatedAt,
        ModerationStatus status,
        Privacy whoCanSee
) { }
