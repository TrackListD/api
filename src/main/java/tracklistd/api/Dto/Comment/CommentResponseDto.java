package tracklistd.api.Dto.Comment;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "DTO com os dados públicos de um comentário")
public record CommentResponseDto(

        Long id,

        @Schema(description = "ID da publicação à qual o comentário pertence", example = "1")
        Long postId,

        Long authorId,

        @Schema(description = "Texto do comentário", example = "Excelente análise!")
        String text,

        LocalDateTime commentDate,
        Long likeCount
) {
}
