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

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "post", target = "post")
    Comment toEntity(CommentRequestDto commentRequestDto, Publication post, User author);

    @Mapping(source = "publicationDate", target = "commentDate")
    @Mapping(source = "post.id", target = "idPost")
    @Mapping(source = "author.id", target = "idAuthor")
    @Mapping(source = "likeCount", target = "likeCount")
    CommentResponseDto toResponseDTO(Comment comment, Long likeCount);

    @Mapping(source = "commentResponseDto", target = "publicData")
    @Mapping(source = "moderationStatus", target = "status")
    CommentOwnerResponseDto toOwnerResponseDTO(Comment comment, CommentResponseDto commentResponseDto);
}
