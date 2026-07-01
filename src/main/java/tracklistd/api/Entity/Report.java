package tracklistd.api.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;
import tracklistd.api.Entity.Interfaces.Reportable;

@Entity
@Getter
@NoArgsConstructor
public class Report implements Reportable {
    // ----------- Class atributes -----------//

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "informer_id", nullable = false)
    private User userInformer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_target_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User userTarget;

    @Column(name = "report_reason")
    private String reason;

    @Column(name = "report_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime reportDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_report", updatable = true)
    private ReportStatus statusReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_target_id")
    private Comment commentTarget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_target_id")
    private Rating ratingTarget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_list_target_id")
    private MediaList mediaListTarget;

    @Enumerated(EnumType.STRING)
    @Column(name = "punishment_type", updatable = true)
    private Punishment punishment;

    // ---------------------------------------//

    // ----------- Constructor -----------//

    // Construtor para denúncia de MediaList
    public Report(User informer, String reason, LocalDateTime date, MediaList mediaListTarget) {
        this.userInformer = informer;
        this.reason = reason;
        this.reportDate = date;
        this.mediaListTarget = mediaListTarget;
        this.statusReport = ReportStatus.PENDING;
    }

    // -- Report constructor for Comment --//
    public Report(User informer, String Reason, LocalDateTime date, Comment comment) {
        this.userInformer = informer;
        this.userTarget = null;
        this.reason = Reason;
        this.reportDate = date;
        this.commentTarget = comment;
        this.ratingTarget = null;
        this.statusReport = ReportStatus.PENDING;
    }

    // -- Report constructor for Rating --//
    public Report(User informer, String Reason, LocalDateTime date, Rating rating) {
        this.userInformer = informer;
        this.userTarget = null;
        this.reason = Reason;
        this.reportDate = date;
        this.commentTarget = null;
        this.ratingTarget = rating;
        this.statusReport = ReportStatus.PENDING;
    }

    // -- Report constructor for Targeted User --//
    public Report(User informer, String Reason, LocalDateTime date, User target) {
        this.userInformer = informer;
        this.userTarget = target;
        this.reason = Reason;
        this.reportDate = date;
        this.commentTarget = null;
        this.ratingTarget = null;
        this.statusReport = ReportStatus.PENDING;
    }

    // ---------------------------------//

    // ----------- Methods -----------//

    public void solveReport(ReportStatus newStatus, Punishment newPunishment) {
        statusReport = newStatus;
        punishment = newPunishment;
    }

    @Override
    public String getContentReported() {
        if (commentTarget != null)
            return commentTarget.getText();

        if (ratingTarget != null)
            return ratingTarget.getReview();

        if (mediaListTarget != null)
            return mediaListTarget.getListName();

        if (userTarget != null)
            return userTarget.getName();

        return null;
    }

    @Override
    public ModerationStatus getStatusModeration() {
        return null;
    }

    @Override
    public Reportable getTarget() {
        if (commentTarget != null)
            return commentTarget;

        if (ratingTarget != null)
            return ratingTarget;

        if (mediaListTarget != null)
            return mediaListTarget;

        if (userTarget != null)
            return userTarget;

        return null;
    }

    // ---------------------------------------//

}
