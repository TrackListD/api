package tracklistd.api.Mapper;

import org.springframework.stereotype.Component;

import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Music;

@Component
public class MediaMapper {

    public MediaMinDTO toMinDTO(Media media) {
        if (media == null) {
            return null;
        }

        String artistName = media.getMainArtist() != null
                ? media.getMainArtist().getName()
                : "Artista Desconhecido";

        String type = switch (media) {
            case Album a -> "album";
            case Music m -> "music";
            default -> "unknown";
        };

        return new MediaMinDTO(
                media.getSpotifyID(),
                media.getTitle(),
                artistName,
                type,
                media.getCoverUrl());
    }
}
