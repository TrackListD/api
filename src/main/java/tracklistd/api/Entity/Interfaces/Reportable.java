package tracklistd.api.Entity.Interfaces;
import tracklistd.api.Entity.Enums.ModerationStatus;

public interface Reportable {
    public String getContentReported();

    public ModerationStatus getStatusModeration();

    public Reportable getTarget();
}
