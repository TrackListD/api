package tracklistd.api.Entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
import tracklistd.api.Entity.Enums.ReportStatus;

@Entity
@Getter
@NoArgsConstructor
public class Report{
    //----------- Class atributes -----------//

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne (fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "informer_id", nullable=false)
    private User userInformer;

    @ManyToOne (fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "user_target_id", nullable=false)
    private User userTarget;

    @Column(name="report_reason")
    private String reason;

    @Column(name="report_date", nullable=false, updatable=false)
    @CreationTimestamp
    private LocalDateTime reportDate;

    @Enumerated (EnumType.STRING)
    @Column(name="status_report", updatable=true)
    private ReportStatus statusReport;

    @ManyToOne (fetch=FetchType.LAZY)
    @JoinColumn(name = "comment_target_id")
    private Comment commentTarget;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="rating_target_id")
    private Rating ratingTarget;

    //---------------------------------------//

    //----------- Constructor -----------//


    //-- Report constructor for Comment --//
    public Report(User informer, User target, String Reason, LocalDateTime date, Comment comment){
        this.userInformer = informer;
        this.userTarget = target;
        this.reason = Reason;
        this.reportDate = date;
        this.commentTarget = comment;
        this.ratingTarget = null;
        this.statusReport = ReportStatus.PENDING;
    }

    //-- Report constructor for Rating --//
    public Report(User informer, User target, String Reason, LocalDateTime date, Rating rating){
        this.userInformer = informer;
        this.userTarget = target;
        this.reason = Reason;
        this.reportDate = date;
        this.commentTarget = null;
        this.ratingTarget = rating;
        this.statusReport = ReportStatus.PENDING;
    }

    //---------------------------------//

    //----------- Methods -----------//


    public void solveReport(ReportStatus newStatus){
        statusReport = newStatus;
    }

    public String getContent() {
        if (commentTarget != null)
            return commentTarget.getText();

        if (ratingTarget != null)
            return ratingTarget.getReview();

        return "An error has occurred uppon getting reported content!";
    }

    //---------------------------------------//

}
