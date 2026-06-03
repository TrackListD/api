package tracklistd.api.Dto.SpotifyAPI;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTokenResponseDTO(
                @JsonProperty("access_token") String accessToken,
                @JsonProperty("token_type") String tokenType,
                @JsonProperty("expires_in") Integer expiresIn) {
}