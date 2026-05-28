package tracklistd.api.Dto.User;

import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import java.time.LocalDate;
import java.util.UUID;

public record UserPerfilResponseDTO(
        UUID id,
        String nome,
        String bio,
        Role role,
        Privacy quemPodeComentar,
        LocalDate dataCriacao,
        boolean estaAtivo
) {
}
