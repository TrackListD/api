package tracklistd.api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tracklistd.api.Dto.MediaList.MediaListOwnerResponseDto;
import tracklistd.api.Dto.MediaList.MediaListRequestDto;
import tracklistd.api.Dto.MediaList.MediaListResponseDto;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.User;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface MediaListMapper {


    @Mapping(target = "favorite",  source = "mediaListRequestDto.isFavorite")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "media",    ignore = true)
    MediaList toEntity(MediaListRequestDto mediaListRequestDto, User author);

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "author.id", target = "idAuthor")
    @Mapping(source = "media", target = "mediaIds")
    MediaListResponseDto toResponseDto(MediaList mediaList);

    //Transforma a Entidade em DTO de response "privado" do criador
    @Mapping(source = "mediaListResponseDto", target = "publicData")
    MediaListOwnerResponseDto toOwnerResponseDTO(MediaList mediaList, MediaListResponseDto mediaListResponseDto);


    default String[] mapMediaSetToStringVector(Set<Media> media)
    {
        if(media == null)
            return null;

        return media.stream()
                .map(Media::getSpotifyID)
                .toArray(String[]::new);
    }
}
