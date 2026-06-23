package tracklistd.api.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tracklistd.api.Dto.User.UserRegisterRequestDTO;
import tracklistd.api.Dto.User.UserUpdatePerfilRequestDTO;
import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Integration.FirebaseAuth.FirebaseFilter;
import tracklistd.api.Integration.FirebaseAuth.Config.SecurityConfig;
import tracklistd.api.Service.FirebaseService;
import tracklistd.api.Service.UserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, FirebaseFilter.class})
@EnableMethodSecurity
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private FirebaseService firebaseService;

    private User testUser;
    private UsernamePasswordAuthenticationToken mockAuth;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Usuário Teste");
        testUser.setIdLoginApi("firebase-uid-xyz");
        testUser.setRole(Role.MEMBER);
        testUser.setWhoCanComment(Privacy.PUBLIC);

        // O token é gerado uma única vez e reaproveitado em todos os testes
        mockAuth = new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList());
    }

    @Test
    @DisplayName("register deve retornar 201 quando dados válidos")
    void register_deveRetornar201_quandoDadosValidos() throws Exception {
        UserRegisterRequestDTO request = new UserRegisterRequestDTO(
                "Usuário Teste",
                "firebase-uid-xyz",
                Role.MEMBER,
                Privacy.PUBLIC,
                "Bio exemplo"
        );

        when(userService.register(any(UserRegisterRequestDTO.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                        .with(authentication(mockAuth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Usuário Teste"));
    }

    @Test
    @DisplayName("findUserById deve retornar 200 e os dados do usuário")
    void findUserById_deveRetornar200() throws Exception {
        when(userService.findUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1")
                        .with(authentication(mockAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Usuário Teste"));
    }

    @Test
    @DisplayName("perfilUpdate deve retornar 200 ao atualizar com sucesso")
    void perfilUpdate_deveRetornar200_aoAtualizarComSucesso() throws Exception {
        UserUpdatePerfilRequestDTO updateDto = new UserUpdatePerfilRequestDTO(
                "Novo Nome", "Nova Bio", Privacy.PUBLIC
        );

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Novo Nome");

        when(userService.perfilUpdate(eq(1L), any(UserUpdatePerfilRequestDTO.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .with(authentication(mockAuth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Nome"));
    }

    @Test
    @DisplayName("followUser deve retornar 204 ao seguir com sucesso")
    void followUser_deveRetornar204() throws Exception {
        mockMvc.perform(post("/api/users/1/follow/2")
                        .with(authentication(mockAuth))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("unfollowUser deve retornar 204 ao deixar de seguir com sucesso")
    void unfollowUser_deveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/users/1/follow/2")
                        .with(authentication(mockAuth))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}