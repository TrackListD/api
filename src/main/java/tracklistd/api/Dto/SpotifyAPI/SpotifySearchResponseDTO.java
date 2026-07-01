package tracklistd.api.Dto.SpotifyAPI;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifySearchResponseDTO(
                @JsonProperty("tracks") SpotifyMusicContainerDTO tracks,
                @JsonProperty("albums") SpotifyAlbumContainerDTO albums,
                @JsonProperty("artists") SpotifyArtistContainerDTO artists) {
}