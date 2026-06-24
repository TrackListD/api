package tracklistd.api.Dto.SpotifyAPI;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyMusicResponseDTO(
        @JsonProperty("id") String id,
        @JsonProperty("name") String title,
        @JsonProperty("release_date") String releaseDateString,
        @JsonProperty("cover_url") List<SpotifyAlbumImageDTO> images,
        @JsonProperty("artists") List<SpotifyArtistResponseDTO> artists,
        @JsonProperty("album_type") String albumType,
        @JsonProperty("genres") List<String> musicGenres,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("duration") Integer durationMS) {

    public String getCoverURL() {
        if (images != null && !images.isEmpty())
            return images.get(0).url();
        return null;
    }
}

record SpotifyAlbumImageDTO(
        @JsonProperty("url") String url,
        @JsonProperty("height") Integer height,
        @JsonProperty("width") Integer width) {
}
