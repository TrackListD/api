package tracklistd.api.Dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;

public record UserRegisterRequestDTO(
        @NotBlank(message = "O nome é obrigatório!") @Size(max = 100, message = "Nome muito longo.") String name,

        @NotBlank(message = "O ID da API de login é obrigatório") String idLoginApi,

        Role role,

        Privacy whoCanComment,

        @Size(max = 500, message = "Máximo de 500 caracteres.") String bio,

        @Size(max = 250, message = "Maximo de 250 caracteres.") String profilePic) {
    public UserRegisterRequestDTO {
        if (whoCanComment == null)
            whoCanComment = Privacy.PUBLIC;
        if (role == null)
            role = Role.MEMBER;
    }
}
