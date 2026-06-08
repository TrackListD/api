package tracklistd.api.Dto.SpotifyAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SpotifySearchResponseDTO(
                @JsonProperty("tracks") SpotifyMusicContainerDTO tracks,
                @JsonProperty("albums") SpotifyAlbumContainerDTO albums,
                @JsonProperty("artists") SpotifyArtistContainerDTO artists) {
}

record SpotifyMusicContainerDTO(
                @JsonProperty("items") List<SpotifyMusicResponseDTO> items) {
}

record SpotifyAlbumContainerDTO(
                @JsonProperty("items") List<SpotifyAlbumResponseDTO> items) {
}

record SpotifyArtistContainerDTO(
                @JsonProperty("items") List<SpotifyArtistResponseDTO> items) {
}