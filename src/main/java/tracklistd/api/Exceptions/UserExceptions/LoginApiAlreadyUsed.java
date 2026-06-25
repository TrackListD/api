package tracklistd.api.Exceptions.UserExceptions;

public class LoginApiAlreadyUsed extends RuntimeException {
    public LoginApiAlreadyUsed() {
        super("Usuário já está cadastrado no sistema!");
    }
}
