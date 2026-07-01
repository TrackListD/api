package tracklistd.api.Dto.Report;

import jakarta.validation.constraints.NotNull;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;

public record ReportUpdateDto(
    @NotNull(message="Must have status report")
    ReportStatus statusReport,
    Punishment punishmentReport
) {}
