package tracklistd.api.Dto.Report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportRequestDTO(
    @NotNull(message="Report must have a informer")
    Long informerId,

    Long userTargetId,

    @NotBlank (message="Report must have a reason")
    String reportReason,

    Long commentTargetId,
    Long ratingTargetId,
    Long postTargetId
)   
{
    public Long getActualRatingId() {
        return ratingTargetId != null ? ratingTargetId : postTargetId;
    }
}



