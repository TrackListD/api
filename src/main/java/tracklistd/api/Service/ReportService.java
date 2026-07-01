package tracklistd.api.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tracklistd.api.Entity.*;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;
import tracklistd.api.Exceptions.ReportExceptions.ReportDoesNotExist;
import tracklistd.api.Exceptions.ReportExceptions.ReportException;
import tracklistd.api.Dto.Report.ReportRequestDTO;
import tracklistd.api.Dto.Report.ReportResponseDto;
import tracklistd.api.Repository.*;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final MediaListRepository mediaListRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository,
        CommentRepository commentRepository,
        RatingRepository ratingRepository,
        MediaListRepository mediaListRepository){
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.ratingRepository = ratingRepository;
        this.mediaListRepository = mediaListRepository;
    }

    public Report createReport(User informer, User target, Comment commentTarget, Rating ratingTarget, MediaList mediaListTarget, String reason) {

        int targetCount = 0;
        if (target != null) targetCount++;
        if (commentTarget != null) targetCount++;
        if (ratingTarget != null) targetCount++;
        if (mediaListTarget != null) targetCount++;

        if (targetCount == 0) {
            throw new ReportException("A denúncia precisa ter pelo menos um alvo.");
        }
        if (targetCount > 1) {
            throw new ReportException("A denúncia não pode ter múltiplos alvos simultâneos. Envie apenas um.");
        }

        LocalDateTime date = LocalDateTime.now();
        if (commentTarget != null) {
            return reportRepository.save(new Report(informer, reason, date, commentTarget));
        } else if (ratingTarget != null) {
            return reportRepository.save(new Report(informer, reason, date, ratingTarget));
        } else if (mediaListTarget != null) {
            return reportRepository.save(new Report(informer, reason, date, mediaListTarget));
        } else {
            return reportRepository.save(new Report(informer, reason, date, target));
        }
    }

    public Report createReport(ReportRequestDTO dto) {
        User informer = userRepository.findById(dto.informerId())
                .orElseThrow(() -> new ReportException("Denunciante não encontrado!"));

        User target = dto.userTargetId() != null ? userRepository.findById(dto.userTargetId()).orElse(null) : null;
        Comment commentTarget = dto.commentTargetId() != null ? commentRepository.findById(dto.commentTargetId()).orElse(null) : null;
        Rating ratingTarget = dto.ratingTargetId() != null ? ratingRepository.findById(dto.ratingTargetId()).orElse(null) : null;
        MediaList mediaListTarget = dto.mediaListTargetId() != null ? mediaListRepository.findById(dto.mediaListTargetId()).orElse(null) : null;

        return createReport(informer, target, commentTarget, ratingTarget,mediaListTarget, dto.reportReason());
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ReportDoesNotExist("Esta denúncia não existe. ID: " + id));
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
    public Report resolveReport(Long reportId, ReportStatus newStatus, Punishment newPunishment){
        Report report = reportRepository.findById(reportId).orElseThrow(
                () -> new ReportDoesNotExist(("Esta denúncia não existe. ID: " + reportId)));

        report.solveReport(newStatus, newPunishment);

        return reportRepository.save(report);
    }

    public List<Report> getReportHistoryUser(Long userId){
        List<Report> list = reportRepository.findByUserTargetId(userId);

        return reportRepository.findByUserTargetId(userId);
    }

    public List<Report> getPendingReportAgainstUser(Long userId){
        return reportRepository.findByUserTargetIdAndStatusReport(userId, ReportStatus.PENDING);
    }
}