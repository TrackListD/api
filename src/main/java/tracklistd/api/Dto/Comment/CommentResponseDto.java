package tracklistd.api.Dto.Comment;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long idPublication,
        Long idAuthor,
        String commentText,
        LocalDateTime commentDate,
        Integer likeCount
) {
}
