package tracklistd.api.Dto.SpotifyAPI;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyMusicResponseDTO(
        @JsonProperty("id") String id,
        @JsonProperty("name") String title,
        @JsonProperty("album") SpotifyTrackAlbumDTO album,
        @JsonProperty("artists") List<SpotifyArtistResponseDTO> artists,
        @JsonProperty("genres") List<String> musicGenres,
        @JsonProperty("duration_ms") Integer durationMS) {

    public String getCoverURL() {
        if (album != null && album.images() != null && !album.images().isEmpty())
            return album.images().get(0).url();
        return null;
    }

    public String getReleaseDate() {
        return album != null ? album.releaseDate() : null;
    }

    public String getAlbumType() {
        return album != null ? album.albumType() : null;
    }
}

record SpotifyTrackAlbumDTO(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("album_type") String albumType,
        @JsonProperty("images") List<SpotifyAlbumImageDTO> images) {
}
