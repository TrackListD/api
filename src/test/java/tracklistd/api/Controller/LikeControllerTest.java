package tracklistd.api.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tracklistd.api.Dto.Like.LikeResponseDTO;
import tracklistd.api.Dto.User.UserMinResponseDTO;
import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Integration.FirebaseAuth.FirebaseFilter;
import tracklistd.api.Integration.FirebaseAuth.Config.SecurityConfig;
import tracklistd.api.Service.FirebaseService;
import tracklistd.api.Service.LikeService;
import tracklistd.api.Service.UserService;

@WebMvcTest(controllers = LikeController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                SecurityConfig.class,
                FirebaseFilter.class
        })
})
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeService likeService;

    @MockitoBean
    private FirebaseService firebaseService;

    @MockitoBean
    private UserService userService;

    private User mockUser;
    private UsernamePasswordAuthenticationToken mockAuth;

    @BeforeEach
    void setUp() {
        mockUser = new User("João Pedro", "auth0|123456", Role.MEMBER, Privacy.PUBLIC);

        mockAuth = new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList());
    }

    @Test
    void shouldToggleLikeWithAuthenticatedUser() throws Exception {
        Long publicationId = 1L;
        LikeResponseDTO expectedResponse = new LikeResponseDTO(true, 1L);

        when(likeService.toggleLike(any(User.class), eq(publicationId))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/publications/{id}/like", publicationId)
                .with(authentication(mockAuth)) // Injeta o usuário mockado no @AuthenticationPrincipal
                .with(csrf()) // Evita o bloqueio de segurança do Spring em métodos POST
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likesCount").value(1));
    }

    @Test
    void shouldReturnUnauthWhenTogglingLikeUnauthenticated() throws Exception {
        // o Spring Security deve bloquear a tentativa de POST
        mockMvc.perform(post("/api/publications/1/like")
                .with(csrf()) // Passamos o CSRF para garantir que o erro seja de AUTENTICAÇÃO e não de falta
                              // de token
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUsersWhoLikedPublication() throws Exception {
        Long publicationId = 1L;
        UserMinResponseDTO userMin = new UserMinResponseDTO(1L, "João Pedro", "imagem-teste");
        List<UserMinResponseDTO> mockList = List.of(userMin);

        when(likeService.getWhoLiked(publicationId)).thenReturn(mockList);

        // Como essa rota é um GET público (seeWhoLiked), não precisamos passar
        // autenticação
        mockMvc.perform(get("/api/publications/{id}/likes", publicationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("João Pedro"));
    }
}