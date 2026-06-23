package tracklistd.api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Music;

@Mapper(componentModel = "spring")
public interface MediaMapper {

    @Mapping(target = "id", source = "spotifyID")
    @Mapping(target = "artist", source = "media", qualifiedByName = "mapArtistName")
    @Mapping(target = "type", source = "media", qualifiedByName = "mapMediaType")
    MediaMinDTO toMinDTO(Media media);

    // Lógica customizada para extrair o nome do artista
    @Named("mapArtistName")
    default String mapArtistName(Media media) {
        if (media == null || media.getMainArtist() == null) {
            return "Artista Desconhecido";
        }
        return media.getMainArtist().getName();
    }

    // Lógica do Pattern Matching (Switch) para definir o tipo
    @Named("mapMediaType")
    default String mapMediaType(Media media) {
        if (media == null) {
            return "unknown";
        }
        return switch (media) {
            case Album a -> "album";
            case Music m -> "music";
            default -> "unknown";
        };
    }
}