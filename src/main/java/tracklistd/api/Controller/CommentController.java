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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Endpoints para gerenciamento de comentários em publicações")
public class CommentController {

    private final CommentService commentService;
    private final PublicationService publicationService;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Criar comentário", description = "Adiciona um comentário a uma publicação existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comentário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "422", description = "Tentativa de comentar na própria publicação")
    })
    public ResponseEntity<CommentResponseDto> createComment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        Publication post = this.publicationService.getPublicationById(commentRequestDto.idPost());

        String commentText = commentRequestDto.text();

        Comment comment = this.commentService.createComment(user, post, commentText);

        Long commentLikeCount = this.commentService.getCommentLikes(comment); // Corrigido para buscar do comentário

        // Como o usuário acabou de criar, likedByMe é false
        CommentResponseDto commentResponseDto = this.commentMapper.toResponseDTO(comment, commentLikeCount, false);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar comentário por ID", description = "Recupera os detalhes completos de um comentário. Apenas para administradores.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comentário retornado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer cargo ADMIN"),
            @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    public ResponseEntity<CommentOwnerResponseDto> getUserComment(
            @AuthenticationPrincipal User user, // Adicionado para checar curtida
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(buildOwnerResponse(id, user));
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "Listar comentários de um post", description = "Lista todos os comentários vinculados a uma publicação específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de comentários retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Publicação não encontrada")
    })
    public ResponseEntity<List<CommentResponseDto>> getPostComments(
            @AuthenticationPrincipal User user, // Adicionado para passar aos mappers
            @PathVariable Long postId) {
        Publication publication = this.publicationService.getPublicationById(postId);

        List<Comment> postComments = this.commentService.getCommentsByPost(publication);

        List<CommentResponseDto> responseDtos = buildCommentResponseList(postComments, user);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar comentários de um usuário", description = "Retorna os comentários feitos por um usuário específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de comentários retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<?> getUserComments(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId) {
        User userWanted = this.userService.findUserById(userId);

        List<Comment> postComments = this.commentService.getCommentsByUser(userWanted);

        List<CommentResponseDto> responseDtos = buildCommentResponseList(postComments, user);

        if (user == null || !Objects.equals(userWanted.getId(), user.getId()))
            return ResponseEntity.status(HttpStatus.OK).body(responseDtos);

        List<CommentOwnerResponseDto> responseOwnerDtos = new ArrayList<>();

        for (int i = 0; i < postComments.size(); i++) {
            responseOwnerDtos.add(this.commentMapper.toOwnerResponseDTO(postComments.get(i), responseDtos.get(i)));
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseOwnerDtos);
    }

    @PatchMapping("/{id}/text")
    @Operation(summary = "Editar texto do comentário", description = "Atualiza o conteúdo de texto de um comentário")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Texto do comentário updated com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é o dono do recurso)"),
            @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    public ResponseEntity<CommentOwnerResponseDto> editCommentText(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid CommentEditRequestDto editRequestDto) {

        this.commentService.editCommentText(editRequestDto.newText(), id, user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir comentário", description = "Remove um comentário existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comentário excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é o dono do recurso)"),
            @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        this.commentService.deleteComment(id, user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/api/comments/post/test")
    public ResponseEntity<LocalDateTime> testDate() {
        return ResponseEntity.ok(LocalDateTime.now());
    }

    // Métodos privados ajustados

    private CommentOwnerResponseDto buildOwnerResponse(Long commentId, User user) {
        Comment comment = this.commentService.getCommentById(commentId);

        Long likeCount = this.commentService.getCommentLikes(comment);

        boolean likedByMe = user != null && comment.getLikes() != null && comment.getLikes().contains(user);

        CommentResponseDto responseDto = this.commentMapper.toResponseDTO(comment, likeCount, likedByMe);

        return this.commentMapper.toOwnerResponseDTO(comment, responseDto);
    }

    @NonNull
    private List<CommentResponseDto> buildCommentResponseList(List<Comment> postComments, User user) {
        Long likeCount;
        List<CommentResponseDto> responseDtos = new ArrayList<>();

        for (Comment comment : postComments) {
            likeCount = this.commentService.getCommentLikes(comment); // Ajustado para puxar as curtidas do comentário

            boolean likedByMe = false;
            if (user != null) {
                likedByMe = this.commentService.hasUserLikedComment(comment.getId(), user.getId());
            }

            CommentResponseDto commentResponseDto = this.commentMapper.toResponseDTO(comment, likeCount, likedByMe);
            responseDtos.add(commentResponseDto);
        }

        return responseDtos;
    }
}