package tracklistd.api.Exceptions.UserExceptions;

public class FriendDoesNotExist extends RuntimeException {
    public FriendDoesNotExist() {
        super("Usuário que você deseja seguir ou deixar de seguir não foi encontrado.");
    }
}
