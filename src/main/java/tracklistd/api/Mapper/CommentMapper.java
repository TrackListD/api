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

    @Mapping(target = "post",               ignore = true) // sem setPost(), populado pelo construtor
    @Mapping(target = "moderationStatus",   ignore = true) // default ACTIVE
    Comment toEntity(CommentRequestDto commentRequestDto, Publication post, User author);

    @Mapping(source = "comment.publicationDate", target = "commentDate")
    @Mapping(source = "comment.post.id", target = "idPost")
    @Mapping(source = "comment.author.id", target = "idAuthor")
    @Mapping(source = "likeCount", target = "likeCount")
    CommentResponseDto toResponseDTO(Comment comment, Long likeCount);

    @Mapping(source = "commentResponseDto", target = "publicData")
    @Mapping(source = "comment.moderationStatus", target = "status")
    @Mapping(source = "comment.updateAt",target = "updateAt")
    CommentOwnerResponseDto toOwnerResponseDTO(Comment comment, CommentResponseDto commentResponseDto);
}
