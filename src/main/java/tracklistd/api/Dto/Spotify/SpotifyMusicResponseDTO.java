package tracklistd.api.Dto.Spotify;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyMusicResponseDTO(
                @JsonProperty("id") String id,
                @JsonProperty("title") String title,
                @JsonProperty("duration_ms") Integer durationMS,
                @JsonProperty("track_number") Integer trackNumber,
                @JsonProperty("artists") List<SpotifyArtistResponseDTO> artists,
                @JsonProperty("album") SpotifyAlbumResponseDTO album) {
}
