package tracklistd.api.Dto.Artist;

import java.util.List;

import tracklistd.api.Dto.Media.AlbumDetailsResponseDTO;

public record ArtistDetailsResponseDTO(
    ArtistMinDTO artist, // Reutilizando a estrutura base
    List<AlbumDetailsResponseDTO> albums
) {}