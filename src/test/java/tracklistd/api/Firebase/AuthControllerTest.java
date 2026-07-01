package tracklistd.api.Firebase;

import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tracklistd.api.Service.FirebaseService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FirebaseService firebaseService;

    @Test
    @DisplayName("Deve registrar ou logar usuário com sucesso quando o token do Firebase for válido")
    public void deveLogarComTokenValido() throws Exception {
        String tokenValido = "token_de_teste_valido_123";

        FirebaseToken mockFirebaseToken = Mockito.mock(FirebaseToken.class);
        Mockito.when(mockFirebaseToken.getUid()).thenReturn("firebase_uid_vini_123");
        Mockito.when(mockFirebaseToken.getName()).thenReturn("Vinicius Teste");
        Mockito.when(mockFirebaseToken.getEmail()).thenReturn("vini@teste.com");

        Mockito.when(firebaseService.verify(tokenValido)).thenReturn(mockFirebaseToken);

        mockMvc.perform(post("/api/auth/login")
                .header("Authorization", "Bearer " + tokenValido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Vinicius Teste"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.moderationStatus").value("ACTIVE"));
    }
}