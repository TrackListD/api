package tracklistd.api.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import tracklistd.api.Entity.Enums.ModerationStatus;


@Entity
@DiscriminatorValue("comment")
@PrimaryKeyJoinColumn(name = "comment_id")
public class Comment extends Publication {

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

    public Comment() {}

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

}
