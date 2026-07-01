package tracklistd.api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Dto.MediaList.MediaListOwnerResponseDto;
import tracklistd.api.Dto.MediaList.MediaListRequestDto;
import tracklistd.api.Dto.MediaList.MediaListResponseDto;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.User;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MediaListMapper {

    @Mapping(source = "mediaList.author.name", target = "authorName")
    @Mapping(source = "mediaList.author.id", target = "authorId")
    @Mapping(source = "mediaList.media", target = "medias")
    @Mapping(target = "totalDurationMs", expression = "java(mediaList.calculateTotalDurationMs())")
    @Mapping(target = "formattedDuration", expression = "java(formatDuration(mediaList))")
    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "commentCount", target = "commentCount")
    @Mapping(source = "likedByMe", target = "likedByMe")
    MediaListResponseDto toResponseDto(MediaList mediaList, Long likeCount, Integer commentCount, boolean likedByMe);

    // Transforma a Entidade em DTO de response "privado" do criador
    @Mapping(source = "mediaListResponseDto", target = "publicData")
    MediaListOwnerResponseDto toOwnerResponseDTO(MediaList mediaList, MediaListResponseDto mediaListResponseDto);

    default Set<MediaMinDTO> mapMediaSetToDtoList(Set<Media> mediaSet) {
        if (mediaSet == null)
            return null;
        return mediaSet.stream()
                .map(MediaMinDTO::new)
                .collect(Collectors.toSet());
    }

    // Método para cuidar da formatação visual do tempo de duração da lista
    default String formatDuration(MediaList mediaList) {
        if (mediaList == null)
            return "0m";

        Integer durationMs = mediaList.calculateTotalDurationMs();
        if (durationMs == null || durationMs == 0)
            return "0m";

        int totalSeconds = durationMs / 1000;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return minutes + "m";
    }
}
