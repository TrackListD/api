package tracklistd.api.Dto.MediaList;

import io.swagger.v3.oas.annotations.media.Schema;
import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Entity.Enums.ListType;

import java.util.Set;

@Schema(description = "DTO com os dados públicos de uma lista de mídias")
public record MediaListResponseDto(

                Long id,

                @Schema(description = "O tipo de mídias contidas na lista", example = "ALBUM", allowableValues = {
                                "ALBUM", "MUSIC" }) ListType typeOfList,

                @Schema(description = "Nome da lista", example = "Meus Álbuns Favoritos") String listName,

                @Schema(description = "Indica se a lista está favoritada pelo autor", example = "false") Boolean isFavorite,

                Long authorId,
                String authorName,

                @Schema(description = "Lista detalhada das mídias contidas na lista") Set<MediaMinDTO> medias,

                @Schema(description = "Duração total em milissegundos (dado bruto)", example = "6300000") Integer totalDurationMs,

                @Schema(description = "Duração total formatada para exibição", example = "1h 45m") String formattedDuration,

                @Schema(description = "Descrição detalhada ou conceito da lista", example = "Os melhores álbuns de rap nacional lançados na última década.") String description,

                @Schema(description = "URL da imagem de capa da lista") String coverImageUrl,

                @Schema(description = "Conjunto de tags associadas à lista para categorização e busca", example = "[\"Rap\", \"Nacional\", \"Classicos\"]") Set<String> tags,

                Long likeCount,
                Integer commentCount,
                boolean likedByMe) {
}
