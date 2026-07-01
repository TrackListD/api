package tracklistd.api.Dto.SpotifyAPI;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyArtistResponseDTO(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("genres") List<String> genres,
        @JsonProperty("images") List<SpotifyArtistImageDTO> images) {

    public String getProfilePictureURL() {
        if (images != null && !images.isEmpty())
            return images.get(0).url();
        return null;
    }
}

record SpotifyArtistImageDTO(
        @JsonProperty("url") String url,
        @JsonProperty("height") Integer height,
        @JsonProperty("width") Integer width) {
}
