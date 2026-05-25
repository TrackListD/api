package tracklistd.api.Dto.MediaList;

import tracklistd.api.Entity.Enums.ListType;

public record MediaListResponseDto(
        ListType typeOfList,
        String listName,
        Boolean isFavorite,
        Long authorId,
        String authorName,
        Long[] mediaIds
) {
}
