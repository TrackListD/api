package tracklistd.api.Dto.Rating;

import io.swagger.v3.oas.annotations.media.Schema;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Enums.Privacy;

import java.time.LocalDateTime;

@Schema(description = "DTO contendo os dados da avaliação mais os campos específicos para o dono")
public record RatingOwnerResponseDto(
        @Schema(description = "Dados públicos da avaliação")
        RatingResponseDto publicData,

        LocalDateTime updatedAt,

        @Schema(description = "Status de moderação da avaliação", example = "ACTIVE", allowableValues = {"ACTIVE", "BANNED", "SUSPENDED", "OCULT"})
        ModerationStatus status,

        @Schema(description = "Define quem pode visualizar a avaliação", example = "PUBLIC", allowableValues = {"PUBLIC", "JUST_FOLLOWERS", "PRIVATE"})
        Privacy whoCanSee
) { }
