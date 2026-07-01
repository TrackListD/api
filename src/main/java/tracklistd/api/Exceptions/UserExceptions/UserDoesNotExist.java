package tracklistd.api.Exceptions.UserExceptions;

public class UserDoesNotExist extends RuntimeException {
    public UserDoesNotExist(Long id) {
        super(("Usuário não encontrado com o ID: " + id));
    }
}
