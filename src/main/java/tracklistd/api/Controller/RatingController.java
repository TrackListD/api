package tracklistd.api.Controller;

import com.google.firebase.auth.FirebaseToken;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tracklistd.api.Dto.Rating.RatingEditRequestDto;
import tracklistd.api.Dto.Rating.RatingOwnerResponseDto;
import tracklistd.api.Dto.Rating.RatingRequestDto;
import tracklistd.api.Dto.Rating.RatingResponseDto;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Mapper.RatingMapper;
import tracklistd.api.Service.AuthService;
import tracklistd.api.Service.MediaService;
import tracklistd.api.Service.RatingService;
import tracklistd.api.Service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "Endpoints para gerenciamento de avaliações e reviews de mídias")
public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;
    private final MediaService mediaService;
    private final RatingMapper ratingMapper;
    private final AuthService authService;


    @PostMapping
    @Operation(summary = "Criar avaliação", description = "Registra uma nova avaliação musical")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Avaliação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "409", description = "Avaliação já cadastrada para esta mídia")
    })
    public ResponseEntity<RatingResponseDto> createRating(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RatingRequestDto ratingRequestDto
    ) {
        Media media = mediaService.getMediaById(ratingRequestDto.targetId());

        Float ratingNote = ratingRequestDto.ratingNote();
        String review = ratingRequestDto.review();
        Privacy whoCanSee = ratingRequestDto.whoCanSee();

        Rating rating = ratingService.createRating(user, media, ratingNote, review, whoCanSee);

        RatingResponseDto response = ratingMapper.toResponseDto(rating, 0, 0L);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar avaliação por ID", description = "Obtém os detalhes públicos ou de proprietário de uma avaliação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliação retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<?> getRating(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        Rating rating = this.ratingService.getRatingById(id);

        Integer commentCount = this.ratingService.getRatingComments(rating);

        Long likeCount = this.ratingService.getRatingLikes(rating);

        RatingResponseDto responseDto = ratingMapper.toResponseDto(rating, commentCount, likeCount);

        if (user == null)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        boolean isOwner = Objects.equals(rating.getAuthorPublication().getId(), user.getId());

        if (isOwner) {
            RatingOwnerResponseDto ownerResponseDto = ratingMapper.toOwnerResponseDTO(rating, responseDto);
            return ResponseEntity.status(HttpStatus.OK).body(ownerResponseDto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar avaliações por usuário", description = "Recupera as avaliações de um usuário respeitando as regras de privacidade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<?> getRatings(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId
    ) {
        User userWantedRatings = this.userService.findUserById(userId);

        if (user == null) {
            List<Rating> publicRatings = this.ratingService.getRatingsByUserPrivacy(userWantedRatings, Privacy.PUBLIC);
            return buildRatingsResponse(publicRatings);
        }

        boolean isOwner = Objects.equals(userWantedRatings.getId(), user.getId());

        if (isOwner) {
            List<Rating> allRatings = this.ratingService.getRatingsByUser(userWantedRatings);
            return buildRatingsResponse(allRatings);
        } else {
            List<Rating> publicRatings = this.ratingService.getRatingsByUserPrivacy(userWantedRatings, Privacy.PUBLIC);
            return buildRatingsResponse(publicRatings);
        }
    }


    @PatchMapping("/{id}/review")
    @Operation(summary = "Editar review", description = "Atualiza o comentário textual de uma avaliação")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Review atualizada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário não autenticado ou não autorizado"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<?> editRatingReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid RatingEditRequestDto.EditReviewRequestDto editDto
    ) {
        if (user == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        this.ratingService.editReview(editDto.newReview(), id, user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));
    }


    @PatchMapping("/{id}/note")
    @Operation(summary = "Editar nota", description = "Atualiza a nota atribuída em uma avaliação")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Nota atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nota inválida fornecida"),
            @ApiResponse(responseCode = "403", description = "Usuário não autenticado ou não autorizado"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<?> editRatingNote(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid RatingEditRequestDto.EditRatingNoteRequestDto editRatingNote
    ) {
        if (user == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        this.ratingService.editRatingNote(editRatingNote.newRatingNote(), id, user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));
    }


    @PatchMapping("/{id}/privacy")
    @Operation(summary = "Editar privacidade", description = "Altera as configurações de privacidade de uma avaliação")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Privacidade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Privacidade inválida fornecida"),
            @ApiResponse(responseCode = "403", description = "Usuário não autenticado ou não autorizado"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<?> editRatingPrivacy(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid RatingEditRequestDto.EditPrivacyRequestDto editPrivacy
    ) {
        if (user == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        this.ratingService.changePrivacy(editPrivacy.newPrivacy(), id, user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir avaliação", description = "Remove uma avaliação existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Avaliação excluída com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário não autenticado ou não autorizado"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<Void> deleteRating(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        this.ratingService.deleteRating(id, user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ---------- Métodos Privados -------

    @NonNull
    private ResponseEntity<?> buildRatingsResponse(List<Rating> ratings) {
        List<RatingResponseDto> responseDtos = new ArrayList<>();

        Integer commentCount;
        Long likeCount;

        for (Rating rating : ratings) {
            commentCount = this.ratingService.getRatingComments(rating);
            likeCount = this.ratingService.getRatingLikes(rating);
            responseDtos.add(this.ratingMapper.toResponseDto(rating, commentCount, likeCount));
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @NonNull
    private RatingOwnerResponseDto buildOwnerResponse(Long ratingId) {
        Rating rating = this.ratingService.getRatingById(ratingId);

        Integer commentCount = this.ratingService.getRatingComments(rating);
        Long likeCount = this.ratingService.getRatingLikes(rating);

        RatingResponseDto responseDto = this.ratingMapper.toResponseDto(rating, commentCount, likeCount);

        return this.ratingMapper.toOwnerResponseDTO(rating, responseDto);
    }
}
