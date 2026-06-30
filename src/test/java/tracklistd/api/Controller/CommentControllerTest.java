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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tracklistd.api.Dto.Comment.CommentEditRequestDto;
import tracklistd.api.Dto.Comment.CommentOwnerResponseDto;
import tracklistd.api.Dto.Comment.CommentRequestDto;
import tracklistd.api.Dto.Comment.CommentResponseDto;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.CommentExceptions.CommentOwershipViolation;
import tracklistd.api.Exceptions.CommentExceptions.SelfCommentException;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Integration.FirebaseAuth.Config.SecurityConfig;
import tracklistd.api.Integration.FirebaseAuth.FirebaseFilter;
import tracklistd.api.Mapper.CommentMapper;
import tracklistd.api.Service.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@Import({ SecurityConfig.class, FirebaseFilter.class })
@EnableMethodSecurity
public class CommentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper()
                        .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        @MockitoBean
        private CommentService commentService;

        @MockitoBean
        private PublicationService publicationService;

        @MockitoBean
        private CommentMapper commentMapper;

        @MockitoBean
        private UserService userService;

        @MockitoBean
        private FirebaseService firebaseService;

        private User testUser;
        private User otherUser;
        private Publication testPost;
        private Comment testComment;
        private CommentResponseDto testResponseDto;
        private CommentOwnerResponseDto testOwnerResponseDto;

        @BeforeEach
        void setUp() {
                testUser = new User();
                testUser.setId(1L);
                testUser.setName("User Test");

                otherUser = new User();
                otherUser.setId(2L);
                otherUser.setName("Other User");

                testPost = Mockito.mock(Publication.class);
                when(testPost.getId()).thenReturn(100L);

                testComment = Mockito.spy(new Comment(testUser, testPost, "This is a comment"));
                doReturn(10L).when(testComment).getId();

                testResponseDto = new CommentResponseDto(
                                10L, 100L, 1L, "This is a comment", LocalDateTime.now(), 5L, true);

                testOwnerResponseDto = new CommentOwnerResponseDto(
                                testResponseDto, ModerationStatus.ACTIVE, LocalDateTime.now());
        }

        // --- POST /api/comments ---

        @Test
        @DisplayName("createComment deve retornar 201 quando dados válidos")
        void createComment_deveRetornar201_quandoDadosValidos() throws Exception {
                CommentRequestDto request = new CommentRequestDto(100L, "This is a comment");

                when(publicationService.getPublicationById(100L)).thenReturn(testPost);
                when(commentService.createComment(any(User.class), any(Publication.class), eq("This is a comment")))
                                .thenReturn(testComment);
                when(commentService.getCommentLikes(testComment)).thenReturn(5L);
                when(commentMapper.toResponseDTO(eq(testComment), eq(5L), anyBoolean())).thenReturn(testResponseDto);

                mockMvc.perform(post("/api/comments")
                                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null,
                                                Collections.emptyList())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.postId").value(100))
                                .andExpect(jsonPath("$.text").value("This is a comment"));
        }

        @Test
        @DisplayName("createComment deve retornar 400 quando texto em branco")
        void createComment_deveRetornar400_quandoTextoEmBranco() throws Exception {
                CommentRequestDto request = new CommentRequestDto(100L, "   ");

                mockMvc.perform(post("/api/comments")
                                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null,
                                                Collections.emptyList())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("createComment deve retornar 422 quando comentar no próprio post")
        void createComment_deveRetornar422_quandoComentarNoProprioPost() throws Exception {
                CommentRequestDto request = new CommentRequestDto(100L, "This is a comment");

                when(publicationService.getPublicationById(100L)).thenReturn(testPost);
                when(commentService.createComment(any(User.class), any(Publication.class), anyString()))
                                .thenThrow(new SelfCommentException());

                mockMvc.perform(post("/api/comments")
                                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null,
                                                Collections.emptyList())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnprocessableEntity());
        }

        // --- GET /api/comments/{id} ---

        @Test
        @DisplayName("getUserComment deve retornar 200 quando usuário for Admin")
        void getUserComment_deveRetornar200_quandoForAdmin() throws Exception {
                when(commentService.getCommentById(10L)).thenReturn(testComment);
                when(commentService.getCommentLikes(testComment)).thenReturn(5L);
                when(commentMapper.toResponseDTO(eq(testComment), eq(5L), anyBoolean())).thenReturn(testResponseDto);
                when(commentMapper.toOwnerResponseDTO(testComment, testResponseDto)).thenReturn(testOwnerResponseDto);

                mockMvc.perform(get("/api/comments/10")
                                .with(authentication(new UsernamePasswordAuthenticationToken(
                                                testUser, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("ACTIVE"))
                                .andExpect(jsonPath("$.publicData.text").value("This is a comment"));
        }

        @Test
        @DisplayName("getUserComment deve retornar 403 quando usuário não for Admin")
        void getUserComment_deveRetornar403_quandoNaoForAdmin() throws Exception {
                mockMvc.perform(get("/api/comments/10")
                                .with(authentication(new UsernamePasswordAuthenticationToken(
                                                testUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))))))
                                .andExpect(status().isForbidden());
        }

        // --- GET /api/comments/post/{postId} ---

        @Test
        @DisplayName("getPostComments deve retornar 200 quando postId válido")
        void getPostComments_deveRetornar200_quandoPostIdValido() throws Exception {
                when(publicationService.getPublicationById(100L)).thenReturn(testPost);
                when(commentService.getCommentsByPost(testPost)).thenReturn(List.of(testComment));
                when(commentService.getCommentLikes(testComment)).thenReturn(5L);
                when(commentMapper.toResponseDTO(eq(testComment), eq(5L), anyBoolean())).thenReturn(testResponseDto);

                mockMvc.perform(get("/api/comments/post/100"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].text").value("This is a comment"));
        }

        @Test
        @DisplayName("getPostComments deve retornar 404 quando post não encontrado")
        void getPostComments_deveRetornar404_quandoPostNaoEncontrado() throws Exception {
                when(publicationService.getPublicationById(999L))
                                .thenThrow(new ResourceNotFoundException("Post not found"));

                mockMvc.perform(get("/api/comments/post/999"))
                                .andExpect(status().isNotFound());
        }

        // --- GET /api/comments/user/{userId} ---

        @Test
        @DisplayName("getUserComments deve retornar todos como Owner quando usuário for dono")
        void getUserComments_deveRetornarTodosComoOwner_quandoUsuarioForDono() throws Exception {
                when(userService.findUserById(1L)).thenReturn(testUser);
                when(commentService.getCommentsByUser(testUser)).thenReturn(List.of(testComment));
                when(commentService.getCommentLikes(testComment)).thenReturn(5L);
                when(commentMapper.toResponseDTO(eq(testComment), eq(5L), anyBoolean())).thenReturn(testResponseDto);
                when(commentMapper.toOwnerResponseDTO(testComment, testResponseDto)).thenReturn(testOwnerResponseDto);

                mockMvc.perform(get("/api/comments/user/1")
                                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null,
                                                Collections.emptyList()))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                                .andExpect(jsonPath("$[0].publicData.text").value("This is a comment"));
        }

        @Test
        @DisplayName("getUserComments deve retornar todos como Public quando usuário não for dono")
        void getUserComments_deveRetornarTodosComoPublic_quandoUsuarioNaoForDono() throws Exception {
                when(userService.findUserById(1L)).thenReturn(testUser);
                when(commentService.getCommentsByUser(testUser)).thenReturn(List.of(testComment));
                when(commentService.getCommentLikes(testComment)).thenReturn(5L);
                when(commentMapper.toResponseDTO(eq(testComment), eq(5L), anyBoolean())).thenReturn(testResponseDto);

                mockMvc.perform(get("/api/comments/user/1")
                                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null,
                                                Collections.emptyList()))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].status").doesNotExist())
                                .andExpect(jsonPath("$[0].text").value("This is a comment"));
        }

        @Test
        @DisplayName("getUserComments deve retornar 404 quando usuário não encontrado")
        void getUserComments_deveRetornar404_quandoUsuarioNaoEncontrado() throws Exception {
                when(userService.findUserById(999L)).thenThrow(new ResourceNotFoundException("User not found"));

                mockMvc.perform(get("/api/comments/user/999"))
                                .andExpect(status().isNotFound());
        }

        // --- PATCH /api/comments/{id}/text ---

        @Test
        @DisplayName("editCommentText deve retornar 202 quando dono edita")
        void editCommentText_deveRetornar202_quandoDonoEdita() throws Exception {
                CommentEditRequestDto request = new CommentEditRequestDto("New comment text");

                when(commentService.getCommentById(10L)).thenReturn(testComment);
                when(commentService.getCommentLikes(testComment)).thenReturn(5L);
                when(commentMapper.toResponseDTO(eq(testComment), eq(5L), anyBoolean())).thenReturn(testResponseDto);
                when(commentMapper.toOwnerResponseDTO(testComment, testResponseDto)).thenReturn(testOwnerResponseDto);

                mockMvc.perform(patch("/api/comments/10/text")
                                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null,
                                                Collections.emptyList())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isAccepted())
                                .andExpect(jsonPath("$.status").value("ACTIVE"));

                verify(commentService).editCommentText("New comment text", 10L, 1L);
        }

        @Test
        @DisplayName("editCommentText deve retornar 400 quando texto for em branco")
        void editCommentText_deveRetornar400_quandoTextoForEmBranco() throws Exception {
                CommentEditRequestDto request = new CommentEditRequestDto("");

                mockMvc.perform(patch("/api/comments/10/text")
                                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null,
                                                Collections.emptyList())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("editCommentText deve retornar 401 quando não autenticado")
        void editCommentText_deveRetornar403_quandoNaoAutenticado() throws Exception {
                CommentEditRequestDto request = new CommentEditRequestDto("New text");

                mockMvc.perform(patch("/api/comments/10/text")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("editCommentText deve retornar 403 quando não for dono")
        void editCommentText_deveRetornar403_quandoNaoForDono() throws Exception {
                CommentEditRequestDto request = new CommentEditRequestDto("New text");

                doThrow(new CommentOwershipViolation())
                                .when(commentService).editCommentText(anyString(), anyLong(), eq(2L));

                mockMvc.perform(patch("/api/comments/10/text")
                                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null,
                                                Collections.emptyList())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());
        }

        // --- DELETE /api/comments/{id} ---

        @Test
        @DisplayName("deleteComment deve retornar 204 quando dono deleta")
        void deleteComment_deveRetornar204_quandoDonoDeleta() throws Exception {
                mockMvc.perform(delete("/api/comments/10")
                                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null,
                                                Collections.emptyList()))))
                                .andExpect(status().isNoContent());

                verify(commentService).deleteComment(10L, 1L);
        }

        @Test
        @DisplayName("deleteComment deve retornar 401 quando não autenticado")
        void deleteComment_deveRetornar403_quandoNaoAutenticado() throws Exception {
                mockMvc.perform(delete("/api/comments/10"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("deleteComment deve retornar 403 quando não for dono")
        void deleteComment_deveRetornar403_quandoNaoForDono() throws Exception {
                doThrow(new CommentOwershipViolation())
                                .when(commentService).deleteComment(10L, 2L);

                mockMvc.perform(delete("/api/comments/10")
                                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null,
                                                Collections.emptyList()))))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("deleteComment deve retornar 404 quando não encontrado")
        void deleteComment_deveRetornar404_quandoCommentNaoEncontrado() throws Exception {
                doThrow(new ResourceNotFoundException("Comment not found"))
                                .when(commentService).deleteComment(99L, 1L);

                mockMvc.perform(delete("/api/comments/99")
                                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null,
                                                Collections.emptyList()))))
                                .andExpect(status().isNotFound());
        }
}
