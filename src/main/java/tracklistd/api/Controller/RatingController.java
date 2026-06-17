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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;
    private final MediaService mediaService;
    private final RatingMapper ratingMapper;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<RatingResponseDto> createRating(@AuthenticationPrincipal FirebaseToken token,
                                                          @Valid @RequestBody RatingRequestDto ratingRequestDto)
    {
        User user = userService.findUserByUid(token.getUid());

        Media media = mediaService.getMediaById(ratingRequestDto.targetId());

        Float ratingNote = ratingRequestDto.ratingNote();
        String review = ratingRequestDto.review();
        Privacy whoCanSee = ratingRequestDto.whoCanSee();

        Rating rating = ratingService.createRating(user,media, ratingNote, review, whoCanSee);

        RatingResponseDto response = ratingMapper.toResponseDto(rating, 0, 0L);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRating(@AuthenticationPrincipal FirebaseToken token,
                                        @PathVariable Long id)
    {

        Rating rating = this.ratingService.getRatingById(id);

        Integer commentCount = this.ratingService.getRatingComments(rating);

        Long likeCount = this.ratingService.getRatingLikes(rating);

        RatingResponseDto responseDto = ratingMapper.toResponseDto(rating, commentCount, likeCount);

        if(token == null)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        User user = userService.findUserByUid(token.getUid());

        boolean isOwner = Objects.equals(rating.getAuthorPublication().getId(), user.getId());

        if(isOwner)
        {
            RatingOwnerResponseDto ownerResponseDto = ratingMapper.toOwnerResponseDTO(rating, responseDto);
            return ResponseEntity.status(HttpStatus.OK).body(ownerResponseDto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRatings(
            @AuthenticationPrincipal FirebaseToken token,
            @PathVariable Long userId
    )
    {

        User userWantedRatings = this.userService.findUserById(userId);

        if(token == null )
        {
            List<Rating> publicRatings = this.ratingService.getRatingsByUserPrivacy(userWantedRatings,Privacy.PUBLIC);

            return buildRatingsResponse(publicRatings);
        }

        User userAuth = this.userService.findUserByUid(token.getUid());

        boolean isOwner = Objects.equals(userWantedRatings.getId(), userAuth.getId());

        if(isOwner)
        {
            List<Rating> allRatings = this.ratingService.getRatingsByUser(userWantedRatings);

            return buildRatingsResponse(allRatings);

        }
        else
        {
            List<Rating> publicRatings = this.ratingService.getRatingsByUserPrivacy(userWantedRatings,Privacy.PUBLIC);

            return buildRatingsResponse(publicRatings);
        }

    }
//    PATCH  /ratings/{id}/review            → editar review

    @PatchMapping("/{id}/review")
    public ResponseEntity<?> editRatingReview
            (
                    @AuthenticationPrincipal FirebaseToken token,
                    @PathVariable Long id,
                    @RequestBody @Valid RatingEditRequestDto.EditReviewRequestDto editDto
                    )
    {
        if(token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        User user = this.userService.findUserByUid(token.getUid());

        this.ratingService.editReview(editDto.newReview(), id , user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));


    }

//    PATCH  /ratings/{id}/note              → editar nota
    @PatchMapping("/{id}/note")
    public ResponseEntity<?> editRatingNote
    (
            @AuthenticationPrincipal FirebaseToken token,
            @PathVariable Long id,
            @RequestBody @Valid RatingEditRequestDto.EditRatingNoteRequestDto editRatingNote
    )
    {
        if(token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        User user = this.userService.findUserByUid(token.getUid());

        this.ratingService.editRatingNote(editRatingNote.newRatingNote(), id, user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));

    }

//    PATCH  /ratings/{id}/privacy           → mudar privacidade
    @PatchMapping("/{id}/privacy")
    public ResponseEntity<?> editRatingPrivacy
    (
            @AuthenticationPrincipal FirebaseToken token,
            @PathVariable Long id,
            @RequestBody @Valid RatingEditRequestDto.EditPrivacyRequestDto editPrivacy
    )
    {
        if(token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        User user = this.userService.findUserByUid(token.getUid());

        this.ratingService.changePrivacy(editPrivacy.newPrivacy(), id, user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));

    }


//    DELETE /ratings/{id}                   → apagar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating
    (
            @AuthenticationPrincipal FirebaseToken token,
            @PathVariable Long id
    )
    {
        if(token == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        User user = this.userService.findUserByUid(token.getUid());

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
