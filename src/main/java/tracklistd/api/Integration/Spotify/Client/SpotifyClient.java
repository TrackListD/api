package tracklistd.api.Integration.Spotify.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import tracklistd.api.Dto.SpotifyAPI.SpotifyAlbumResponseDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifyMusicResponseDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifySearchResponseDTO;
import tracklistd.api.Integration.Spotify.Auth.SpotifyAuth;

@Component
public class SpotifyClient {

    private final RestTemplate restTemplate;

    private final SpotifyAuth authManager;

    private static final String SPOTIFY_API_URL = "https://api.spotify.com/v1";

    @Autowired
    public SpotifyClient(RestTemplate restTemplate, SpotifyAuth authManager) {
        this.restTemplate = restTemplate;
        this.authManager = authManager;
    }

    public SpotifySearchResponseDTO search(String query) {
        String url = SPOTIFY_API_URL + "/search?q=" + query + "&type=track,album,artist&limit=10";
        
        ResponseEntity<SpotifySearchResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                createRequestWithToken(),
                SpotifySearchResponseDTO.class
        );
        
        return response.getBody();
    }

    public SpotifyMusicResponseDTO getMusicById(String spotifyId) {
        String url = SPOTIFY_API_URL + "/tracks/" + spotifyId;
        
        ResponseEntity<SpotifyMusicResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                createRequestWithToken(),
                SpotifyMusicResponseDTO.class
        );
        
        return response.getBody();
    }

    public SpotifyAlbumResponseDTO getAlbumById(String spotifyId) {
        String url = SPOTIFY_API_URL + "/albums/" + spotifyId;
        
        ResponseEntity<SpotifyAlbumResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                createRequestWithToken(),
                SpotifyAlbumResponseDTO.class
        );
        
        return response.getBody();
    }

    private HttpEntity<Void> createRequestWithToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authManager.getAccessToken()); 
        return new HttpEntity<>(headers);
    }
}