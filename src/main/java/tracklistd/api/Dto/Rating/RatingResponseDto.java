package tracklistd.api.Dto.Rating;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO de transferência de dados de uma Avaliação")
public record RatingResponseDto(
        Long authorId,

        @Schema(description = "ID que a spotifyAPI gera do Album ou Musica que será Avaliado", example = "6rqhFgbbKwnb9MLmUQDhG6")
        String targetId,

        LocalDateTime publicationDate,
        Float ratingNote,

        @Schema(description = "Texto da Avaliação", example = "Amo esse Album! Produção Incrível!!!")
        String review,

        String authorName,

        @Schema(description = "Nome do Album ou Musica que será Avaliado", example = "Billie Jean")
        String targetName,

        Long likeCount,
        Integer commentCount
) { }

