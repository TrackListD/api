package tracklistd.api.Service;

import jakarta.transaction.Transactional;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;
import tracklistd.api.Entity.Report;
import tracklistd.api.Entity.User;

import java.time.LocalDateTime;

public class AdminService {

    private final UserService userService;
    private final ReportService reportService;
    private final CommentService commentService;
    private final RatingService ratingService;

    public AdminService(UserService userService, ReportService reportService, CommentService commentService, RatingService ratingService) {
        this.userService = userService;
        this.reportService = reportService;
        this.commentService = commentService;
        this.ratingService = ratingService;
    }

    @Transactional
    public Report moderateReport(Long reportID, ReportStatus status, Punishment punishment, Long daysOfSuspension){
        Report report = reportService.resolveReport(reportID, status, punishment);

        if(status == ReportStatus.RESOLVED){
            User userTarget = null;

            if(report.getCommentTarget() != null){
                userTarget = commentService.hideComment(report.getCommentTarget());
            }
            else if(report.getRatingTarget() != null){
                userTarget = ratingService.hideRating(report.getRatingTarget());
            }
            else if(report.getUserTarget() != null){
                userTarget = report.getUserTarget();
            }

            if(userTarget != null){
                userService.applyPunishment(userTarget, punishment, daysOfSuspension);
            }
        }

        return report;
    }
}
