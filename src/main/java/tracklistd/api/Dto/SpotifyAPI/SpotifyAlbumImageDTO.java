package tracklistd.api.Dto.SpotifyAPI;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyAlbumImageDTO(
                @JsonProperty("url") String url,
                @JsonProperty("height") Integer height,
                @JsonProperty("width") Integer width) {
}
