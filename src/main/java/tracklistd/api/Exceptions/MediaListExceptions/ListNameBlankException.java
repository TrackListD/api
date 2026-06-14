package tracklistd.api.Exceptions.MediaListExceptions;

public class ListNameBlankException extends MediaListaException {
    public ListNameBlankException() {
        super("A lista deve conter um nome");
    }
}
