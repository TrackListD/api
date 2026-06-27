package tracklistd.api.Dto.Rating;

import io.swagger.v3.oas.annotations.media.Schema;
import tracklistd.api.Dto.Media.MediaMinDTO;

import java.time.LocalDateTime;

@Schema(description = "DTO de transferência de dados de uma Avaliação")
public record RatingResponseDto(

        Long id,

        Long authorId,

        @Schema(description = "Dados consolidados da mídia que foi avaliada")
        MediaMinDTO targetMedia,

        LocalDateTime publicationDate,
        Float ratingNote,

        @Schema(description = "Texto da Avaliação", example = "Amo esse Album! Produção Incrível!!!")
        String review,

        String authorName,

        Long likeCount,
        Integer commentCount
) { }

