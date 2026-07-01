package tracklistd.api.Dto.SpotifyAPI;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyMusicContainerDTO(
                @JsonProperty("items") List<SpotifyMusicResponseDTO> items) {
}
