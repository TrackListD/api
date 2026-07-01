package tracklistd.api.Repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tracklistd.api.Entity.Report;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // Retorna Reports com determinado ReportStatus
    List<Report> findByStatusReport(ReportStatus statusReport);

    // Retorna Reports com determinado Punishment
    List<Report> findByPunishment(Punishment punishment);

    // Retorna quantidade total de Reports com determinado ReportStatus
    long countByStatusReport(ReportStatus statusReport);

    // Retorna Reports contra um usuário
    List<Report> findByUserTargetId(Long userId);

    // Retorna Reports feitos contra um comentário
    List<Report> findByCommentTargetId(Long commentId);

    // Retorna todos Reports feito por um usuário
    List<Report> findByUserInformerId(Long userId);

    // Retorna todos os Reports feitos contra determinado Rating
    List<Report> findByRatingTarget_Id(Long ratingId);

    // Retorna Reports contra um usuário com determinado Status
    List<Report> findByUserTargetIdAndStatusReport(Long userId, ReportStatus status);

}
