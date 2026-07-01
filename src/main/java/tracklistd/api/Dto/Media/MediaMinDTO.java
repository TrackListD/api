package tracklistd.api.Dto.Media;

import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.Album;

public record MediaMinDTO
        (
        String id,
        String title,
        String artist,
        String type, // "album" ou "music"
        String coverUrl,
        Integer durationMs,
        String formattedDuration
)
    {
        public MediaMinDTO(Media media) {
            this(
                    media.getSpotifyID(),
                    media.getTitle(),
                    media.getMainArtist() != null ? media.getMainArtist().getName() : "Artista Desconhecido",
                    determineType(media),
                    media.getCoverUrl(),
                    media.getTotalDurationMs(),
                    formatDuration(media.getTotalDurationMs())
                    );
    }

    private static String determineType(Media media)
    {
        if (media instanceof Album)
            return "album";
        if (media instanceof Music)
            return "music";
        return "unknown";
    }

    private static String formatDuration(Integer durationMs)
    {
        if (durationMs == null || durationMs == 0) return "0m";
        int totalSeconds = durationMs / 1000;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        return hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
    }
}