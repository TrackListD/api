package tracklistd.api.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User informer = userRepository.findById(dto.informerId())
            .orElseThrow(() -> new ReportException("Informer not found!"));
        

        User target = dto.userTargetId() != null ? userRepository.findById(dto.userTargetId()).orElse(null) : null;
        Comment commentTarget = dto.commentTargetId() != null ? commentRepository.findById(dto.commentTargetId()).orElse(null) : null;
        Long ratingId = dto.ratingTargetId() != null ? dto.ratingTargetId() : dto.postTargetId();
        Rating ratingTarget = ratingId != null ? ratingRepository.findById(ratingId).orElse(null) : null;
        
        LocalDateTime date = LocalDateTime.now();
        Report report = null;
        String reason = dto.reportReason();
        
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

    @Transactional
    public ReportResponseDto resolveReport(Long reportId, ReportStatus newStatus, Punishment newPunishment){
        Report report = reportRepository.findById(reportId).orElseThrow( 
            () -> new ReportDoesNotExist(("This report dos not exist. ID: " + reportId)));

        report.solveReport(newStatus, newPunishment);

        Report updateReport = reportRepository.save(report);

        return new ReportResponseDto(updateReport);
    }

    public List<ReportResponseDto> getReportHistoryUser(Long userId){
        List<Report> list = reportRepository.findByUserTargetId(userId);

        return list.stream()
                .map(ReportResponseDto::new)
                .toList();
    }

    public List<ReportResponseDto> getPendingReportAgainstUser(Long userId){
        List<Report> list = reportRepository.findByUserTargetIdAndStatusReport(userId, ReportStatus.PENDING);

        return list.stream()
                .map(ReportResponseDto::new)
                .toList();
    }
}
