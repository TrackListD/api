package tracklistd.api.Dto.User;

import jakarta.validation.constraints.Size;
import tracklistd.api.Entity.Enums.Privacy;

public record UserUpdatePerfilRequestDTO(
        @Size(max = 100, message = "Nome muito longo.") String name,
        @Size(max = 500, message = "Máximo de 500 caracteres.") String bio,
        Privacy whoCanComment,
        String favoriteArtistSpotifyId,
        String favoriteMusicSpotifyId,
        String favoriteAlbumSpotifyId) {
}