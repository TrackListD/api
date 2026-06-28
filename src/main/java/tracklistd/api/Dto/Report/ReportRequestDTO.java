package tracklistd.api.Dto.Report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportRequestDTO(
    @NotNull(message="O ID do denunciante é obrigatório.")
    Long informerId,

    Long userTargetId,

    @NotBlank (message="O motivo da denúncia é obrigatório.")
    String reportReason,

    Long commentTargetId,
    Long ratingTargetId
)   
{}