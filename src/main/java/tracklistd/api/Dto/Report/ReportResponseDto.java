package tracklistd.api.Dto.Report;

import java.time.LocalDateTime;

import tracklistd.api.Entity.Enums.ReportStatus;

public record ReportResponseDto(
    Long id,
    Long informerId,
    Long userTargetId,
    String reason,
    LocalDateTime reportDate,
    ReportStatus reportStatus,
    String reportedContent
)

{}
