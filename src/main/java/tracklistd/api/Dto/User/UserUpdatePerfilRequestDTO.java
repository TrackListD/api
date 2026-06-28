package tracklistd.api.Dto.User;

import jakarta.validation.constraints.Size;
import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Artist;
import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.Enums.Privacy;

public record UserUpdatePerfilRequestDTO(
                @Size(max = 100, message = "Nome muito longo.") String name,

                @Size(max = 500, message = "Máximo de 500 caracteres.") String bio,

                Privacy whoCanComment,

                Artist favoriteArtist,
                Music favoriteMusic,
                Album favoriteAlbum

) {
}
