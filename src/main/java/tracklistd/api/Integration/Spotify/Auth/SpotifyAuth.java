package tracklistd.api.Integration.Spotify.Auth;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import tracklistd.api.Dto.SpotifyAPI.SpotifyTokenResponseDTO;

@Service
public class SpotifyAuth {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    private String accessToken;

    // Roda de 50 em 50 minutos para se certificar de que o token da api sempre vai
    // estar disponível
    @Scheduled(fixedRate = 3000000)
    public void renovarToken() {
        String url = "https://accounts.spotify.com/api/token";

        // Passa o clientID e o clientSecret para base64
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedAuth);

        // Requisição
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            SpotifyTokenResponseDTO response = restTemplate.postForObject(url, request, SpotifyTokenResponseDTO.class);
            if (response != null) {
                this.accessToken = response.accessToken();
                System.out.println("Token Spotify atualizado");
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar token Spotify: " + e.getMessage());
        }
    }

    public String getAccessToken() {
        if (this.accessToken == null) {
            renovarToken();
        }
        return this.accessToken;
    }
}