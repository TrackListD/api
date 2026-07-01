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
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Interfaces.Reportable;

@Entity
@DiscriminatorValue("rating")
@PrimaryKeyJoinColumn(name = "rating_id")
public class Rating extends Publication implements Reportable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private Media target;

    @Column(name = "rating_note", updatable = true, nullable = false)
    private Float ratingNote;

    @Column(name = "review", updatable = true)
    private String review;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, updatable = true)
    private ModerationStatus status = ModerationStatus.ACTIVE;

    public Rating() {
    }

    public Rating(User author, Media target, Float ratingNote, String review, Privacy whoCanSee) {
        super(author, whoCanSee);
        this.target = target;
        this.ratingNote = ratingNote;
        this.review = review;
    }

    public Media getTargetMedia() {
        return target;
    }

    public void setTarget(Media media){this.target = media;}

    public Float getRatingNote() {
        return ratingNote;
    }

    public void editNote(Float ratingNote) {
        this.ratingNote = ratingNote;
    }

    public String getReview() {
        return review;
    }

    public void editReview(String newReview) {
        this.review = newReview;
    }

    public ModerationStatus getStatus() {
        return status;
    }

    public void setStatus(ModerationStatus newStatus) {
        this.status = newStatus;
    }

    @Override
    public String getContentReported() {
        return review;
    }

    @Override
    public ModerationStatus getStatusModeration() {
        return status;
    }

    @Override
    public Reportable getTarget() {
        return author;
    }

    public void setMediaTarget(Media media) {
        this.target = media;
    }
}
