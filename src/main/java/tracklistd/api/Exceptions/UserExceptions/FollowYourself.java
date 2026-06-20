package tracklistd.api.Exceptions.UserExceptions;

public class FollowYourself extends RuntimeException {
    public FollowYourself() {
        super("Você não pode seguir ou deixar de seguir você mesmo!");
    }
}
