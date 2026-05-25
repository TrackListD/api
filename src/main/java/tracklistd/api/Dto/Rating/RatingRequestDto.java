package tracklistd.api.Dto.Rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import tracklistd.api.Entity.Enums.Privacy;

public record RatingRequestDto(
        @NotNull(message = "A Avaliação deve ter um alvo")
        Long idTarget,
        @Min(value = 0, message = "A nota deve ser maior ou igual a 0")
        @Max(value = 5, message = "A nota deve ser menor ou igual a 5")
        @NotNull(message = "A Avaliação deve ter uma nota")
        Float ratingNote,
        String review,
        Privacy whoCanSee) {}
