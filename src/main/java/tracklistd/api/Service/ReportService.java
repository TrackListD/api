package tracklistd.api.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;
import tracklistd.api.Exceptions.ReportExceptions.ReportDoesNotExist;
import tracklistd.api.Exceptions.ReportExceptions.ReportException;
import tracklistd.api.Dto.Report.ReportRequestDTO;
import tracklistd.api.Dto.Report.ReportResponseDto;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.Report;
import tracklistd.api.Repository.CommentRepository;
import tracklistd.api.Repository.RatingRepository;
import tracklistd.api.Repository.ReportRepository;
import tracklistd.api.Repository.UserRepository;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository,
        CommentRepository commentRepository,
        RatingRepository ratingRepository){
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.ratingRepository = ratingRepository;

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

    public Report createReport(ReportRequestDTO dto){
        User informer = userRepository.findById(dto.informer_id())
            .orElseThrow(() -> new ReportException("Informer not found!"));
        

        User target = dto.user_target_id() != null ? userRepository.findById(dto.user_target_id()).orElse(null) : null;
        Comment commentTarget = dto.comment_target_id() != null ? commentRepository.findById(dto.comment_target_id()).orElse(null) : null;
        Rating ratingTarget = dto.rating_target_id() != null ? ratingRepository.findById(dto.rating_target_id()).orElse(null) : null;
        
        LocalDateTime date = LocalDateTime.now();
        Report report = null;
        String reason = dto.report_reason();
        
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

    public List<ReportResponseDto> getPendingReportsDto() {
        List<Report> pendingReports = reportRepository.findByStatusReport(ReportStatus.PENDING);
        
        // Converte a lista de entidades para lista de DTOs antes de mandar para a Controller
        return pendingReports.stream()
                .map(ReportResponseDto::new)
                .toList();
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
