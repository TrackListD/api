package tracklistd.api.Dto.Report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportRequestDTO(
    @NotNull(message="Report must have a informer")
    long informer_id,

    @NotNull(message="Report must have a user target")
    long user_target_id,

    @NotBlank (message="Report must have a reason")
    String report_reason_id,

    Long comment_target_id,
    Long rating_target_id
)   
{}



