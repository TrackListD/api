package tracklistd.api.Dto.Comment;

import io.swagger.v3.oas.annotations.media.Schema;
import tracklistd.api.Entity.Enums.ModerationStatus;

import java.time.LocalDateTime;

@Schema(description = "DTO com os dados do comentário para o proprietário")
public record CommentOwnerResponseDto(
        @Schema(description = "Dados públicos do comentário")
        CommentResponseDto publicData,

        @Schema(description = "Status de moderação do comentário", example = "ACTIVE", allowableValues = {"ACTIVE", "BANNED", "SUSPENDED", "OCULT"})
        ModerationStatus status,

        LocalDateTime updateAt
) {}
