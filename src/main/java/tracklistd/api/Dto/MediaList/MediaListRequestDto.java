package tracklistd.api.Dto.MediaList;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.Privacy;

@Schema(description = "DTO que recebe os dados necessários para criar/atualizar uma lista de mídias")
public record MediaListRequestDto(
        @NotNull(message = "A lista deve ter um tipo(Album ou Musica)")
        @Schema(description = "O tipo de mídias que a lista conterá", example = "ALBUM", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"ALBUM", "MUSIC"})
        ListType typeOfList,

        @NotBlank(message = "A lista deve ter um nome")
        @Schema(description = "Nome da lista de mídias", example = "Meus Álbuns Favoritos", requiredMode = Schema.RequiredMode.REQUIRED)
        String listName,

        @Schema(description = "Indica se a lista está marcada como favorita", example = "false")
        Boolean isFavorite,

        @Schema(description = "Define quem pode visualizar a lista de mídias", example = "PUBLIC", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"PUBLIC", "JUST_FOLLOWERS", "PRIVATE"})
        Privacy whoCanSee,

        @Schema(description = "Lista de IDs das mídias (IDs do banco de dados) a serem adicionadas", example = "[1, 2, 3]")
        Long[] mediaIds
) {
}
