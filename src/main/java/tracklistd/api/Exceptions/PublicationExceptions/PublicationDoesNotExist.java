package tracklistd.api.Exceptions.PublicationExceptions;

public class PublicationDoesNotExist extends PublicationException {
    public PublicationDoesNotExist(Long id) {
        super("A publicação com o id referenciado não existe");
    }
}
