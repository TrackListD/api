package tracklistd.api.Dto.MediaList;

import io.swagger.v3.oas.annotations.media.Schema;
import tracklistd.api.Entity.Enums.Privacy;

@Schema(description = "DTO contendo os dados da lista de mídias mais os campos específicos para o dono")
public record MediaListOwnerResponseDto(
        @Schema(description = "Dados públicos da lista de mídias")
        MediaListResponseDto publicData,

        @Schema(description = "Define quem pode visualizar a lista de mídias", example = "PUBLIC", allowableValues = {"PUBLIC", "JUST_FOLLOWERS", "PRIVATE"})
        Privacy whoCanSee
) {
}
