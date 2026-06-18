package tracklistd.api.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tracklistd.api.Dto.Comment.CommentEditRequestDto;
import tracklistd.api.Dto.Comment.CommentOwnerResponseDto;
import tracklistd.api.Dto.Comment.CommentRequestDto;
import tracklistd.api.Dto.Comment.CommentResponseDto;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;
import tracklistd.api.Mapper.CommentMapper;
import tracklistd.api.Service.CommentService;
import tracklistd.api.Service.PublicationService;
import tracklistd.api.Service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PublicationService publicationService;
    private final CommentMapper commentMapper;
    private final UserService userService;

    //Criar
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CommentRequestDto commentRequestDto
            )
    {
        Publication post = this.publicationService.getPublicationById(commentRequestDto.idPost());

        String commentText = commentRequestDto.text();

        Comment comment = this.commentService.createComment(user,post,commentText);

        Long commentLikeCount = this.commentService.getCommentLikes(post);

        CommentResponseDto commentResponseDto = this.commentMapper.toResponseDTO(comment, commentLikeCount);

        return  ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDto);
    }

    //buscar um comentário (uso : moderação)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentOwnerResponseDto> getUserComment
    (
            @PathVariable Long id
    )
    {
        return ResponseEntity.status(HttpStatus.OK).body(buildOwnerResponse(id));
    }


    //listar comentários de um post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getPostComments
    (
            @PathVariable Long postId
    )
    {
        Publication publication = this.publicationService.getPublicationById(postId);

        List<Comment> postComments = this.commentService.getCommentsByPost(publication);

        List<CommentResponseDto> responseDtos = buildCommentResponseList(postComments);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);

    }

    //listar comentários de um usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserComments
    (
            @AuthenticationPrincipal User user,
            @PathVariable Long userId
    )
    {

        User userWanted = this.userService.findUserById(userId);

        List<Comment> postComments = this.commentService.getCommentsByUser(userWanted);

        List<CommentResponseDto> responseDtos = buildCommentResponseList(postComments);

        if(!Objects.equals(userWanted.getId(), user.getId()))
            return ResponseEntity.status(HttpStatus.OK).body(responseDtos);


        List<CommentOwnerResponseDto> responseOwnerDtos = new ArrayList<>();

        for (int i = 0; i < postComments.size(); i++) {
            responseOwnerDtos.add(this.commentMapper.toOwnerResponseDTO(postComments.get(i), responseDtos.get(i)));
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseOwnerDtos);

    }

    //editar texto
    @PatchMapping("/{id}/text")
    public ResponseEntity<CommentOwnerResponseDto> editCommentText
    (
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid CommentEditRequestDto editRequestDto
    )

    {
        if(user == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        this.commentService.editCommentText(editRequestDto.newText(), id,user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));
    }

    //apagar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment
    (
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    )

    {

        if(user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        this.commentService.deleteComment(id, user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }


    // Métodos privados
    private CommentOwnerResponseDto buildOwnerResponse(Long commentId) {

        Comment comment = this.commentService.getCommentById(commentId);

        Long likeCount = this.commentService.getCommentLikes(comment.getPost());

        CommentResponseDto responseDto = this.commentMapper.toResponseDTO(comment, likeCount);

        return this.commentMapper.toOwnerResponseDTO(comment, responseDto);
    }

    @NonNull
    private List<CommentResponseDto> buildCommentResponseList(List<Comment> postComments) {
        Long likeCount;
        List<CommentResponseDto> responseDtos = new ArrayList<>();

        for(Comment comment : postComments)
        {

            likeCount = this.commentService.getCommentLikes(comment.getPost());

            CommentResponseDto commentResponseDto = this.commentMapper.toResponseDTO(comment,likeCount);
            responseDtos.add(commentResponseDto);
        }

        return responseDtos;
    }

}
