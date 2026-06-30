package tracklistd.api.Dto.Rating;

import io.swagger.v3.oas.annotations.media.Schema;
import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Dto.User.UserMinResponseDTO;
import java.time.LocalDateTime;

@Schema(description = "DTO de transferência de dados de uma Avaliação")
public record RatingResponseDto(
                Long id,
                @Schema(description = "Dados resumidos do autor da avaliação") UserMinResponseDTO author,
                @Schema(description = "Dados consolidados da mídia que foi avaliada") MediaMinDTO targetMedia,
                LocalDateTime publicationDate,
                Float ratingNote,
                @Schema(description = "Texto da Avaliação", example = "Amo esse Album! Produção Incrível!!!") String review,
                Long likeCount,
                Integer commentCount) {
}