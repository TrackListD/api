package tracklistd.api.Exceptions.PublicationExceptions;

import tracklistd.api.Entity.Enums.ModerationStatus;

public class ContentUnavailableException extends RuntimeException {

    private final ModerationStatus status;

    public ContentUnavailableException(ModerationStatus status) {
        super("Conteúdo indisponível (status: " + status + ")");
        this.status = status;
    }

    public ContentUnavailableException(String message, ModerationStatus status) {
        super(message);
        this.status = status;
    }

    public ModerationStatus getStatus() {
        return status;
    }
}