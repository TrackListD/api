package tracklistd.api.Integration.Spotify.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import tracklistd.api.Dto.SpotifyAPI.SpotifySearchResponseDTO;
import tracklistd.api.Integration.Spotify.Auth.SpotifyAuth;

@Component
public class SpotifyClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SpotifyAuth authService;

    private static final String BASE_URL = "https://api.spotify.com/v1";

    // Procura pela busca feita no banco de dados, caso não encontrado, busca pela API do Spotify e salva no banco de dados
    public SpotifySearchResponseDTO searchByString(String query) {
        String url = BASE_URL + "/search?q=" + query + "&type=track,album&limit=10";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<SpotifySearchResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                SpotifySearchResponseDTO.class);

        return response.getBody();
    }
}