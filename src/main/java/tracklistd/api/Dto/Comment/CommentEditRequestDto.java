package tracklistd.api.Dto.Comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentEditRequestDto(
        @NotNull @NotBlank String newText
) {
}
