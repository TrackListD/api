package tracklistd.api.Dto.User;

import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import java.time.LocalDate;

public record UserPerfilResponseDTO(
        Long id,
        String name,
        String bio,
        Role role,
        Privacy whoCanComment,
        LocalDate creationDate,
        boolean estaAtivo
) {
}
