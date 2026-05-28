package tracklistd.api.Dto.User;

import jakarta.validation.constraints.Size;
import tracklistd.api.Entity.Enums.Privacy;

public record UserUpdatePerfilRequestDTO(
        @Size(max = 100, message = "Nome muito longo.")
        String nome,

        @Size(max = 500, message = "Máximo de 500 caracteres.")
        String bio,

        Privacy quemPodeComentar
) {
}
