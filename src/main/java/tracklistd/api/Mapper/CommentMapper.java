package tracklistd.api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tracklistd.api.Dto.Comment.CommentOwnerResponseDto;
import tracklistd.api.Dto.Comment.CommentRequestDto;
import tracklistd.api.Dto.Comment.CommentResponseDto;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CommentMapper {

    @Mapping(source = "comment.publicationDate", target = "commentDate")
    @Mapping(source = "comment.post.id", target = "postId")
    @Mapping(source = "comment.author", target = "author")
    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "likedByMe", target = "likedByMe")
    CommentResponseDto toResponseDTO(Comment comment, Long likeCount, Boolean likedByMe);

    @Mapping(source = "commentResponseDto", target = "publicData")
    @Mapping(source = "comment.moderationStatus", target = "status")
    @Mapping(source = "comment.updateAt", target = "updateAt")
    CommentOwnerResponseDto toOwnerResponseDTO(Comment comment, CommentResponseDto commentResponseDto);
}
