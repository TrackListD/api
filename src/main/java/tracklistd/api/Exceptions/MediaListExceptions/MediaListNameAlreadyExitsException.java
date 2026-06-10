package tracklistd.api.Exceptions.MediaListExceptions;

public class MediaListNameAlreadyExitsException extends RuntimeException {
    public MediaListNameAlreadyExitsException(String listName) {
        super("Já existe uma lista com esse nome: " + listName);
    }
}
