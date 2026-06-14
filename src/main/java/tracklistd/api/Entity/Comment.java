package tracklistd.api.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Interfaces.Reportable;

@Entity
@DiscriminatorValue("comment")
@PrimaryKeyJoinColumn(name = "comment_id")
public class Comment extends Publication implements Reportable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Publication post;

    @Column(name = "text", nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, updatable = true)
    private ModerationStatus moderationStatus = ModerationStatus.ACTIVE;

    public Comment(User autor, Publication post, String text) {
        super(autor);
        this.post = post;
        this.text = text;
    }

    public Comment() {
    }

    public Publication getPost() {
        return post;
    }

    public String getText() {
        return text;
    }

    public void editText(String newText) {
        this.text = newText;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus status) {
        this.moderationStatus = status;
    }

    @Override
    public String getContentReported() {
        return text;
    }

    @Override
    public ModerationStatus getStatusModeration() {
        return moderationStatus;
    }

    @Override
    public Reportable getTarget() {
        return this.author;
    }

}
