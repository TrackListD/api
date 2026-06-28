package tracklistd.api.Dto.Report;

import java.time.LocalDateTime;

import tracklistd.api.Entity.Report;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;

public record ReportResponseDto(
    Long id,
    Long informerId,
    Long userTargetId,
    String reason,
    LocalDateTime reportDate,
    ReportStatus reportStatus,
    Punishment punishment,
    String reportedContent

    
){

    public ReportResponseDto(Report report){
        this(report.getId(),
        report.getUserInformer().getId(),
        report.getUserTarget() != null ? report.getUserTarget().getId() : null,
        report.getReason(),
        report.getReportDate(),
        report.getStatusReport(),
        report.getPunishment(),
        report.getContentReported());
    }

}
