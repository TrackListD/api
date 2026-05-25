package tracklistd.api.Dto.MediaList;

import tracklistd.api.Entity.Enums.Privacy;

public record MediaListOwnerResponseDto(
        MediaListResponseDto publidData,
        Privacy whoCanSee
) {
}
