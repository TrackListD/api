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
public class Rating extends Publication implements Reportable{

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

    @Enumerated(EnumType.STRING)
    @Column(name = "who_can_see", nullable = false, updatable = true)
    private Privacy whoCanSee = Privacy.PUBLIC;

    public Rating(){}

    public Rating(User author, Media target, Float ratingNote, String review, Privacy whoCanSee) {
        super(author);
        this.target = target;
        this.ratingNote = ratingNote;
        this.review = review;
        this.whoCanSee = whoCanSee;
    }

    public Media getTargetMedia() {
        return target;
    }

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

    public Privacy getPrivacy() {
        return whoCanSee;
    }

    public void setPrivacy(Privacy whoCanSee) {
        this.whoCanSee = whoCanSee;
    }

    @Override
    public String getContentReported() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getContentReported'");
    }

    @Override
    public ModerationStatus getStatusModeration() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStatusModeration'");
    }

    @Override
    public Reportable getTarget() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTarget'");
    }
}
