package tracklistd.api.Integration.Spotify.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import tracklistd.api.Dto.Spotify.SpotifyMusicResponseDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifySearchResponseDTO;
import tracklistd.api.Integration.Spotify.Auth.SpotifyAuth;

@Component
public class SpotifyClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SpotifyAuth authService;

    private static final String BASE_URL = "https://api.spotify.com/v1";

    // Busca pelo id do spotify
    public SpotifyMusicResponseDTO getTrackById(String trackId) {
        String url = BASE_URL + "/tracks/" + trackId;
        return executeGet(url, SpotifyMusicResponseDTO.class);
    }

    public SpotifySearchResponseDTO search(String query) {
        String url = BASE_URL + "/search?q=" + query + "&type=track,album&limit=10";
        return executeGet(url, SpotifySearchResponseDTO.class);
    }

    private <T> T executeGet(String url, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<T> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                responseType);

        return response.getBody();
    }
}