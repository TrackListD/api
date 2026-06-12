package tracklistd.api.Dto.Comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO que recebe os dados necessários para criar um Comentário")
public record CommentRequestDto(
        @NotNull(message = "Um comentario precisa pertencer a um post")
        @Schema(description = "ID da publicação (Post/Avaliação) que está sendo comentada", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long idPost,

        @NotBlank(message = "O comentário deve ter um texto")
        @Schema(description = "Conteúdo textual do comentário", example = "Excelente análise!", requiredMode = Schema.RequiredMode.REQUIRED)
        String text
) {
}
