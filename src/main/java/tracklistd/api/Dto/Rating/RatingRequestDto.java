package tracklistd.api.Dto.Rating;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import tracklistd.api.Entity.Enums.Privacy;

@Schema(description = "DTO que recebe e valida os dados necessarios para criar uma Avaliação")
public record RatingRequestDto(
        @NotNull(message = "A Avaliação deve ter um alvo")
        @Schema(description = "O id que o spotifyAPI gera da midia que será avaliada ou seja, do Album ou Musica",
                example = "6rqhFgbbKwnb9MLmUQDhG6",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String targetId,

        @Min(value = 0, message = "A nota deve ser maior ou igual a 0")
        @Max(value = 5, message = "A nota deve ser menor ou igual a 5")
        @NotNull(message = "A Avaliação deve ter uma nota")
        @Schema(description = "A nota da Avaliação", example = "4.5", requiredMode = Schema.RequiredMode.REQUIRED)
        Float ratingNote,

        @Schema(description = "Texto da Avaliação", example = "Amo esse Album! Produção Incrível!!!", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String review,

        @Schema(description = "Enum que determina quem pode ver a Avaliação", example = "PUBLIC", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"PUBLIC", "JUST_FOLLOWERS","PRIVATE"})
        Privacy whoCanSee)
{}
