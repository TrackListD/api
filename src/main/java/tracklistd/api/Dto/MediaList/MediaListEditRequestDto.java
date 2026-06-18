package tracklistd.api.Dto.MediaList;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import tracklistd.api.Entity.Enums.Privacy;

public final class MediaListEditRequestDto{

    private MediaListEditRequestDto(){}

    public record EditReviewRequestDto(String newReview) {}
    public record EditNameRequestDto(String newName) {}
    public record EditPrivacyRequestDto(@NotNull Privacy newPrivacy) {}
}
