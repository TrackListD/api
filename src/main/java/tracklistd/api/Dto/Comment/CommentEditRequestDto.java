package tracklistd.api.Dto.Comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para edição do texto de um comentário")
public record CommentEditRequestDto(
        @NotNull
        @NotBlank
        @Schema(description = "Novo texto para o comentário", example = "Comentário atualizado!", requiredMode = Schema.RequiredMode.REQUIRED)
        String newText
) {
}
