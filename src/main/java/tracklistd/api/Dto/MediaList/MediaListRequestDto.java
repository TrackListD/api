package tracklistd.api.Dto.MediaList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.Privacy;

public record MediaListRequestDto(
        @NotNull(message = "A lista deve ter um tipo(Album ou Musica)")
        ListType typeOfList,

        @NotBlank(message = "A lista deve ter um nome")
        String listName,

        Boolean isFavorite,
        Privacy whoCanSee,
        Long[] mediaIds
) {
}
