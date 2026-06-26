package tracklistd.api.Dto.MediaList;

import io.swagger.v3.oas.annotations.media.Schema;
import tracklistd.api.Entity.Enums.ListType;

@Schema(description = "DTO com os dados públicos de uma lista de mídias")
public record MediaListResponseDto(

        Long id,

        @Schema(description = "O tipo de mídias contidas na lista", example = "ALBUM", allowableValues = {"ALBUM", "MUSIC"})
        ListType typeOfList,

        @Schema(description = "Nome da lista", example = "Meus Álbuns Favoritos")
        String listName,

        @Schema(description = "Indica se a lista está favoritada pelo autor", example = "false")
        Boolean isFavorite,

        Long authorId,
        String authorName,

        @Schema(description = "Array com os IDs das mídias (IDs do Spotify) contidas na lista", example = "[\"6rqhFgbbKwnb9MLmUQDhG6\"]")
        String[] mediaIds
) {
}
