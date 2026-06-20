package tracklistd.api.Dto.Report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportRequestDTO(
    @NotNull(message="Report must have a informer")
    Long informer_id,

    @NotNull(message="Report must have a user target")
    Long user_target_id,

    @NotBlank (message="Report must have a reason")
    String report_reason,

    Long comment_target_id,
    Long rating_target_id
)   
{}



