package tracklistd.api.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tracklistd.api.Dto.Rating.RatingEditRequestDto;
import tracklistd.api.Dto.Rating.RatingOwnerResponseDto;
import tracklistd.api.Dto.Rating.RatingRequestDto;
import tracklistd.api.Dto.Rating.RatingResponseDto;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.RatingsExceptions.RatingAlreadyExists;
import tracklistd.api.Exceptions.RatingsExceptions.RatingOwnershipViolation;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Integration.FirebaseAuth.Config.SecurityConfig;
import tracklistd.api.Integration.FirebaseAuth.FirebaseFilter;
import tracklistd.api.Mapper.RatingMapper;
import tracklistd.api.Service.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
@Import({SecurityConfig.class, FirebaseFilter.class})
public class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private RatingService ratingService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private MediaService mediaService;

    @MockitoBean
    private RatingMapper ratingMapper;

    @MockitoBean
    private FirebaseService firebaseService;

    private User testUser;
    private User otherUser;
    private Media testMedia;
    private Rating testRating;
    private RatingResponseDto testResponseDto;
    private RatingOwnerResponseDto testOwnerResponseDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("User Test");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Other User");

        testMedia = Mockito.mock(Media.class);
        when(testMedia.getTitle()).thenReturn("Media Title");
        
        testRating = Mockito.spy(new Rating(testUser, testMedia, 4.5f, "Excellent review", Privacy.PUBLIC));
        doReturn(10L).when(testRating).getId();

        testResponseDto = new RatingResponseDto(
                1L, "media-123", LocalDateTime.now(), 4.5f, "Excellent review",
                "User Test", "Media Title", 5L, 2
        );

        testOwnerResponseDto = new RatingOwnerResponseDto(
                testResponseDto, LocalDateTime.now(), ModerationStatus.ACTIVE, Privacy.PUBLIC
        );
    }

    // --- POST /api/ratings ---

    @Test
    @DisplayName("createRating deve retornar 201 quando dados válidos")
    void createRating_deveRetornar201_quandoDadosValidos() throws Exception {
        RatingRequestDto request = new RatingRequestDto("media-123", 4.5f, "Excellent review", Privacy.PUBLIC);

        when(mediaService.getMediaById("media-123")).thenReturn(testMedia);
        when(ratingService.createRating(any(User.class), any(Media.class), eq(4.5f), eq("Excellent review"), eq(Privacy.PUBLIC)))
                .thenReturn(testRating);
        when(ratingMapper.toResponseDto(any(Rating.class), anyInt(), anyLong())).thenReturn(testResponseDto);

        mockMvc.perform(post("/api/ratings")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.targetId").value("media-123"))
                .andExpect(jsonPath("$.ratingNote").value(4.5));
    }

    @Test
    @DisplayName("createRating deve retornar 400 quando dados inválidos")
    void createRating_deveRetornar400_quandoNotaInvalida() throws Exception {
        RatingRequestDto request = new RatingRequestDto("media-123", 6.0f, "Excellent review", Privacy.PUBLIC);

        mockMvc.perform(post("/api/ratings")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("createRating deve retornar 409 quando avaliação já existe")
    void createRating_deveRetornar409_quandoAvaliacaoJaExiste() throws Exception {
        RatingRequestDto request = new RatingRequestDto("media-123", 4.5f, "Excellent review", Privacy.PUBLIC);

        RatingAlreadyExists exception = new RatingAlreadyExists(testMedia);
        when(mediaService.getMediaById("media-123")).thenReturn(testMedia);
        when(ratingService.createRating(any(User.class), any(Media.class), any(Float.class), any(String.class), any(Privacy.class)))
                .thenThrow(exception);

        mockMvc.perform(post("/api/ratings")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // --- GET /api/ratings/{id} ---

    @Test
    @DisplayName("getRating deve retornar 200 Owner quando usuário for dono")
    void getRating_deveRetornar200Owner_quandoUsuarioForDono() throws Exception {
        when(ratingService.getRatingById(10L)).thenReturn(testRating);
        when(ratingService.getRatingComments(testRating)).thenReturn(2);
        when(ratingService.getRatingLikes(testRating)).thenReturn(5L);
        when(ratingMapper.toResponseDto(testRating, 2, 5L)).thenReturn(testResponseDto);
        when(ratingMapper.toOwnerResponseDTO(testRating, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(get("/api/ratings/10")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whoCanSee").value("PUBLIC"))
                .andExpect(jsonPath("$.publicDto.targetId").value("media-123"));
    }

    @Test
    @DisplayName("getRating deve retornar 200 Public quando usuário não for dono")
    void getRating_deveRetornar200Public_quandoUsuarioNaoForDono() throws Exception {
        when(ratingService.getRatingById(10L)).thenReturn(testRating);
        when(ratingService.getRatingComments(testRating)).thenReturn(2);
        when(ratingService.getRatingLikes(testRating)).thenReturn(5L);
        when(ratingMapper.toResponseDto(testRating, 2, 5L)).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/ratings/10")
                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null, Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whoCanSee").doesNotExist())
                .andExpect(jsonPath("$.targetId").value("media-123"));
    }

    @Test
    @DisplayName("getRating deve retornar 200 Public quando não autenticado")
    void getRating_deveRetornar200Public_quandoNaoAutenticado() throws Exception {
        when(ratingService.getRatingById(10L)).thenReturn(testRating);
        when(ratingService.getRatingComments(testRating)).thenReturn(2);
        when(ratingService.getRatingLikes(testRating)).thenReturn(5L);
        when(ratingMapper.toResponseDto(testRating, 2, 5L)).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/ratings/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whoCanSee").doesNotExist())
                .andExpect(jsonPath("$.targetId").value("media-123"));
    }

    @Test
    @DisplayName("getRating deve retornar 404 quando avaliação não encontrada")
    void getRating_deveRetornar404_quandoRatingNaoEncontrado() throws Exception {
        when(ratingService.getRatingById(99L)).thenThrow(new ResourceNotFoundException("Rating not found"));

        mockMvc.perform(get("/api/ratings/99"))
                .andExpect(status().isNotFound());
    }

    // --- GET /api/ratings/user/{userId} ---

    @Test
    @DisplayName("getRatings deve retornar todos quando usuário for dono")
    void getRatings_deveRetornarTodos_quandoUsuarioForDono() throws Exception {
        when(userService.findUserById(1L)).thenReturn(testUser);
        when(ratingService.getRatingsByUser(testUser)).thenReturn(List.of(testRating));
        when(ratingService.getRatingComments(testRating)).thenReturn(2);
        when(ratingService.getRatingLikes(testRating)).thenReturn(5L);
        when(ratingMapper.toResponseDto(testRating, 2, 5L)).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/ratings/user/1")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].targetId").value("media-123"));
    }

    @Test
    @DisplayName("getRatings deve retornar apenas públicos quando usuário não for dono")
    void getRatings_deveRetornarApenasPublicos_quandoUsuarioNaoForDono() throws Exception {
        when(userService.findUserById(1L)).thenReturn(testUser);
        when(ratingService.getRatingsByUserPrivacy(testUser, Privacy.PUBLIC)).thenReturn(List.of(testRating));
        when(ratingService.getRatingComments(testRating)).thenReturn(2);
        when(ratingService.getRatingLikes(testRating)).thenReturn(5L);
        when(ratingMapper.toResponseDto(testRating, 2, 5L)).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/ratings/user/1" )
                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null, Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].targetId").value("media-123"));
    }

    @Test
    @DisplayName("getRatings deve retornar apenas públicos quando não autenticado")
    void getRatings_deveRetornarApenasPublicos_quandoNaoAutenticado() throws Exception {
        when(userService.findUserById(1L)).thenReturn(testUser);
        when(ratingService.getRatingsByUserPrivacy(testUser, Privacy.PUBLIC)).thenReturn(List.of(testRating));
        when(ratingService.getRatingComments(testRating)).thenReturn(2);
        when(ratingService.getRatingLikes(testRating)).thenReturn(5L);
        when(ratingMapper.toResponseDto(testRating, 2, 5L)).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/ratings/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].targetId").value("media-123"));
    }

    @Test
    @DisplayName("getRatings deve retornar 404 quando usuário não encontrado")
    void getRatings_deveRetornar404_quandoUsuarioNaoEncontrado() throws Exception {
        when(userService.findUserById(99L)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/ratings/user/99"))
                .andExpect(status().isNotFound());
    }

    // --- PATCH /api/ratings/{id}/review ---

    @Test
    @DisplayName("editRatingReview deve retornar 202 quando dono edita")
    void editRatingReview_deveRetornar202_quandoDonoEdita() throws Exception {
        RatingEditRequestDto.EditReviewRequestDto request = new RatingEditRequestDto.EditReviewRequestDto("New review text");

        when(ratingService.getRatingById(10L)).thenReturn(testRating);
        when(ratingService.getRatingComments(testRating)).thenReturn(2);
        when(ratingService.getRatingLikes(testRating)).thenReturn(5L);
        when(ratingMapper.toResponseDto(testRating, 2, 5L)).thenReturn(testResponseDto);
        when(ratingMapper.toOwnerResponseDTO(testRating, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(patch("/api/ratings/10/review")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.whoCanSee").value("PUBLIC"));

        verify(ratingService).editReview("New review text", 10L, 1L);
    }

    @Test
    @DisplayName("editRatingReview deve retornar 403 quando não autenticado")
    void editRatingReview_deveRetornar403_quandoNaoAutenticado() throws Exception {
        RatingEditRequestDto.EditReviewRequestDto request = new RatingEditRequestDto.EditReviewRequestDto("New review text");

        mockMvc.perform(patch("/api/ratings/10/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("editRatingReview deve retornar 403 quando não for dono")
    void editRatingReview_deveRetornar403_quandoNaoForDono() throws Exception {
        RatingEditRequestDto.EditReviewRequestDto request = new RatingEditRequestDto.EditReviewRequestDto("New review text");

        doThrow(new RatingOwnershipViolation())
                .when(ratingService).editReview(anyString(), anyLong(), eq(2L));

        mockMvc.perform(patch("/api/ratings/10/review")
                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // --- PATCH /api/ratings/{id}/note ---

    @Test
    @DisplayName("editRatingNote deve retornar 202 quando dono edita")
    void editRatingNote_deveRetornar202_quandoDonoEdita() throws Exception {
        RatingEditRequestDto.EditRatingNoteRequestDto request = new RatingEditRequestDto.EditRatingNoteRequestDto(3.5f);

        when(ratingService.getRatingById(10L)).thenReturn(testRating);
        when(ratingService.getRatingComments(testRating)).thenReturn(2);
        when(ratingService.getRatingLikes(testRating)).thenReturn(5L);
        when(ratingMapper.toResponseDto(testRating, 2, 5L)).thenReturn(testResponseDto);
        when(ratingMapper.toOwnerResponseDTO(testRating, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(patch("/api/ratings/10/note")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(ratingService).editRatingNote(3.5f, 10L, 1L);
    }

    @Test
    @DisplayName("editRatingNote deve retornar 400 quando nota inválida")
    void editRatingNote_deveRetornar400_quandoNotaInvalida() throws Exception {
        RatingEditRequestDto.EditRatingNoteRequestDto request = new RatingEditRequestDto.EditRatingNoteRequestDto(6.0f);

        mockMvc.perform(patch("/api/ratings/10/note")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("editRatingNote deve retornar 403 quando não autenticado")
    void editRatingNote_deveRetornar403_quandoNaoAutenticado() throws Exception {
        RatingEditRequestDto.EditRatingNoteRequestDto request = new RatingEditRequestDto.EditRatingNoteRequestDto(3.5f);

        mockMvc.perform(patch("/api/ratings/10/note")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // --- PATCH /api/ratings/{id}/privacy ---

    @Test
    @DisplayName("editRatingPrivacy deve retornar 202 quando dono edita")
    void editRatingPrivacy_deveRetornar202_quandoDonoEdita() throws Exception {
        RatingEditRequestDto.EditPrivacyRequestDto request = new RatingEditRequestDto.EditPrivacyRequestDto(Privacy.PRIVATE);

        when(ratingService.getRatingById(10L)).thenReturn(testRating);
        when(ratingService.getRatingComments(testRating)).thenReturn(2);
        when(ratingService.getRatingLikes(testRating)).thenReturn(5L);
        when(ratingMapper.toResponseDto(testRating, 2, 5L)).thenReturn(testResponseDto);
        when(ratingMapper.toOwnerResponseDTO(testRating, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(patch("/api/ratings/10/privacy")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(ratingService).changePrivacy(Privacy.PRIVATE, 10L, 1L);
    }

    @Test
    @DisplayName("editRatingPrivacy deve retornar 400 quando privacidade nula")
    void editRatingPrivacy_deveRetornar400_quandoPrivacidadeNula() throws Exception {
        RatingEditRequestDto.EditPrivacyRequestDto request = new RatingEditRequestDto.EditPrivacyRequestDto(null);

        mockMvc.perform(patch("/api/ratings/10/privacy")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("editRatingPrivacy deve retornar 403 quando não autenticado")
    void editRatingPrivacy_deveRetornar403_quandoNaoAutenticado() throws Exception {
        RatingEditRequestDto.EditPrivacyRequestDto request = new RatingEditRequestDto.EditPrivacyRequestDto(Privacy.PRIVATE);

        mockMvc.perform(patch("/api/ratings/10/privacy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // --- DELETE /api/ratings/{id} ---

    @Test
    @DisplayName("deleteRating deve retornar 204 quando dono deleta")
    void deleteRating_deveRetornar204_quandoDonoDeleta() throws Exception {
        mockMvc.perform(delete("/api/ratings/10")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isNoContent());

        verify(ratingService).deleteRating(10L, 1L);
    }

    @Test
    @DisplayName("deleteRating deve retornar 403 quando não autenticado")
    void deleteRating_deveRetornar403_quandoNaoAutenticado() throws Exception {
        mockMvc.perform(delete("/api/ratings/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deleteRating deve retornar 403 quando não for dono")
    void deleteRating_deveRetornar403_quandoNaoForDono() throws Exception {
        doThrow(new RatingOwnershipViolation())
                .when(ratingService).deleteRating(10L, 2L);

        mockMvc.perform(delete("/api/ratings/10")
                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null, Collections.emptyList()))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deleteRating deve retornar 404 quando não encontrado")
    void deleteRating_deveRetornar404_quandoRatingNaoEncontrado() throws Exception {
        doThrow(new ResourceNotFoundException("Rating not found"))
                .when(ratingService).deleteRating(99L, 1L);

        mockMvc.perform(delete("/api/ratings/99")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isNotFound());
    }
}
