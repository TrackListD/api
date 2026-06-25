package tracklistd.api.Dto.Rating;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import tracklistd.api.Entity.Enums.Privacy;

public final class RatingEditRequestDto {

    private RatingEditRequestDto() {}

    @Schema(description = "DTO para edição do texto da avaliação")
    public record EditReviewRequestDto(
            @Schema(description = "Novo texto da avaliação (review)", example = "Nova review do álbum!")
            String newReview
    ) {}

    @Schema(description = "DTO para edição da nota da avaliação")
    public record EditRatingNoteRequestDto(
            @NotNull
            @Min(0)
            @Max(5)
            @Schema(description = "Nova nota da avaliação (de 0 a 5)", example = "4.2", requiredMode = Schema.RequiredMode.REQUIRED)
            Float newRatingNote
    ) {}

    @Schema(description = "DTO para alteração de privacidade da avaliação")
    public record EditPrivacyRequestDto(
            @NotNull
            @Schema(description = "Nova privacidade da avaliação", example = "PUBLIC", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"PUBLIC", "JUST_FOLLOWERS", "PRIVATE"})
            Privacy newPrivacy
    ) {}
}
