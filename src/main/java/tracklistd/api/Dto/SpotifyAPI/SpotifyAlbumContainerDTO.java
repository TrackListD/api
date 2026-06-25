package tracklistd.api.Dto.SpotifyAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SpotifyAlbumContainerDTO(
                @JsonProperty("items") List<SpotifyAlbumResponseDTO> items) {
}