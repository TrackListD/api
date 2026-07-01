package tracklistd.api.Dto.Admin;

import jakarta.validation.constraints.NotNull;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;
public record AdminModerateReportRequestDTO(
        @NotNull(message="O status da denúncia é obrigatório.")
        ReportStatus status,

        Punishment punishment,

        Long daysOfSuspension
) {}