package tracklistd.api.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;
import tracklistd.api.Exceptions.ReportExceptions.ReportDoesNotExist;
import tracklistd.api.Exceptions.ReportExceptions.ReportException;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.Report;
import tracklistd.api.Repository.ReportRepository;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository){
        this.reportRepository = reportRepository;
    }

    public Report createReport(User informer, User target, Comment commentTarget, Rating ratingTarget, String reason){
        
        LocalDateTime date = LocalDateTime.now();
        Report report = null;

        if (commentTarget != null){
            report = new Report(informer, reason, date, commentTarget);
        }
        else if (ratingTarget != null){
            report = new Report(informer, reason, date, ratingTarget);
        }
        else if (target != null){
            report = new Report(informer, reason, date, target);
        }

        if (report == null)
            throw new ReportException("Reporte sem alvo!");
        reportRepository.save(report);
        return report;
    }

    public List<Report> getPendingReports(){
        return reportRepository.findByStatusReport(ReportStatus.PENDING);
    }

    public Report resolveReport(Long reportId, ReportStatus newStatus, Punishment newPunishment){
        Report report = reportRepository.findById(reportId).orElseThrow( 
            () -> new ReportDoesNotExist(("This report dos not exist. ID: " + reportId)));

        report.solveReport(newStatus, newPunishment);

        return report;
    }

    public List<Report> getReportHistoryUser(Long userId){
        return reportRepository.findByUserTargetId(userId);
    }
}
