package tracklistd.api.Dto.Media;

public record AlbumDetailsResponseDTO(
        String spotifyID,
        String name,
        String releaseDate,
        String imageUrl
) {}
