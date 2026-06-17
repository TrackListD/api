package tracklistd.api.Dto.Rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tracklistd.api.Entity.Enums.Privacy;

public final class RatingEditRequestDto{

    private RatingEditRequestDto(){}

    public record EditReviewRequestDto(String newReview) {}
    public record EditRatingNoteRequestDto(@NotNull @Min(0) @Max(5) Float newRatingNote) {}
    public record EditPrivacyRequestDto(@NotNull Privacy newPrivacy) {}
}
