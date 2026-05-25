package tracklistd.api.Dto.Comment;

import tracklistd.api.Entity.Enums.ModerationStatus;

import java.time.LocalDateTime;


public record CommentOwnerResponseDto(
        CommentResponseDto publicData,
        ModerationStatus status,
        LocalDateTime updateAt
) {}
