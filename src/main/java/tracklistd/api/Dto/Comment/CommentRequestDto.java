package tracklistd.api.Dto.Comment;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDto(
        Long idPost,
        @NotBlank(message = "O comentário deve ter um texto")
        String text
) {
}
