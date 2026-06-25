package tracklistd.api.Integration.Spotify.Client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import tracklistd.api.Dto.SpotifyAPI.SpotifyAlbumContainerDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifyAlbumResponseDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifyArtistResponseDTO;
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

    public SpotifyArtistResponseDTO getArtistById(String spotifyId) {
        String url = SPOTIFY_API_URL + "/artists/" + spotifyId;
        return restTemplate.exchange(url, HttpMethod.GET, createRequestWithToken(), SpotifyArtistResponseDTO.class).getBody();
    }

    private HttpEntity<Void> createRequestWithToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authManager.getAccessToken()); 
        return new HttpEntity<>(headers);
    }

    public List<SpotifyAlbumResponseDTO> getArtistAlbums(String artistSpotifyId) {
        String url = SPOTIFY_API_URL + "/artists/" + artistSpotifyId + "/albums?include_groups=album,single&limit=20";

        ResponseEntity<SpotifyAlbumContainerDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                createRequestWithToken(),
                SpotifyAlbumContainerDTO.class
        );
    
        return response.getBody() != null ? response.getBody().items() : List.of();
    }
}