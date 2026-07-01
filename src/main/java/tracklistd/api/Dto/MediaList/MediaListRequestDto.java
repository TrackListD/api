package tracklistd.api.Dto.MediaList;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.Privacy;

import java.util.Set;

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
        String[] mediaIds,

        @Size(max = 1000, message = "A descrição não pode passar de 1000 caracteres")
        @Schema(description = "Descrição detalhada ou conceito da lista", example = "Os melhores álbuns de rap nacional lançados na última década.")
        String description,

        @Size(max = 255, message = "A URL da imagem de capa deve ter no máximo 255 caracteres")
        @Schema(description = "URL da imagem de capa")
        String coverImageUrl,

        @Size(max = 5, message = "A lista pode ter no máximo 5 tags")
        @Schema(description = "Tags de categorização da lista para buscas", example = "[\"Rap\", \"Nacional\", \"Classicos\"]")
        Set<String> tags
) {
}
