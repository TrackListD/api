package tracklistd.api.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tracklistd.api.Dto.SpotifyAPI.SpotifySearchResponseDTO;
import tracklistd.api.Integration.Spotify.Client.SpotifyClient;

@Service
public class SpotifyService {

    @Autowired
    private SpotifyClient spotifyClient;

    public SpotifySearchResponseDTO search(String query) {
        if (query == null || query.strip().isEmpty())
            throw new IllegalArgumentException("Não é possóvel buscar por uma query vazia");

        SpotifySearchResponseDTO data = spotifyClient.searchByString(query);
        return data;
    }
}
