package tracklistd.api.Dto.Comment;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "DTO com os dados públicos de um comentário")
public record CommentResponseDto(
        @Schema(description = "ID da publicação à qual o comentário pertence", example = "1")
        Long idPublication,

        Long idAuthor,

        @Schema(description = "Texto do comentário", example = "Excelente análise!")
        String text,

        LocalDateTime commentDate,
        Integer likeCount
) {
}
