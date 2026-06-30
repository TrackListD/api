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
    Long ratingTargetId,
    Long postTargetId
)   
<<<<<<< HEAD
{
    public Long getActualRatingId() {
        return ratingTargetId != null ? ratingTargetId : postTargetId;
    }
}



=======
{}
>>>>>>> 14a14386bce6b268c6e08b73dceced5235b5c58c
