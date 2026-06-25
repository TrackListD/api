package tracklistd.api.Dto.Media;

import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.Album;

public record MediaMinDTO(
        String id,
        String title,
        String artist,
        String type, // "album" ou "music"
        String coverUrl) {
    public MediaMinDTO(Media media) {
        this(
                media.getSpotifyID(),
                media.getTitle(),
                media.getMainArtist() != null ? media.getMainArtist().getName() : "Artista Desconhecido",
                determineType(media),
                media.getCoverUrl());
    }

    private static String determineType(Media media) {
        if (media instanceof Album)
            return "album";
        if (media instanceof Music)
            return "music";
        return "unknown";
    }
}