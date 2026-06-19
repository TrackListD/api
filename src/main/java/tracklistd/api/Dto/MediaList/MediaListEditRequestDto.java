package tracklistd.api.Dto.MediaList;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import tracklistd.api.Entity.Enums.Privacy;

public final class MediaListEditRequestDto {

    private MediaListEditRequestDto() {}

    @Schema(description = "DTO para edição da review de uma lista de mídias")
    public record EditReviewRequestDto(
            @Schema(description = "Nova review para a lista", example = "Melhores álbuns de rock!")
            String newReview
    ) {}

    @Schema(description = "DTO para renomear uma lista de mídias")
    public record EditNameRequestDto(
            @Schema(description = "Novo nome para a lista", example = "Álbuns favoritos atualizado")
            String newName
    ) {}

    @Schema(description = "DTO para alteração da privacidade de uma lista de mídias")
    public record EditPrivacyRequestDto(
            @NotNull
            @Schema(description = "Nova privacidade da lista", example = "PUBLIC", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"PUBLIC", "JUST_FOLLOWERS", "PRIVATE"})
            Privacy newPrivacy
    ) {}
}
