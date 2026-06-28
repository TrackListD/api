package tracklistd.api.Firebase;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FirebaseService firebaseService;

    /*
     * @Test
     * 
     * @DisplayName("Deve barrar requisição em rota protegida quando não houver Token Bearer"
     * )
     * public void deveBarrarRequisicaoSemToken() throws Exception {
     * // Tentando acessar o login sem passar o header Authorization
     * mockMvc.perform(post("/api/auth/login"))
     * .andExpect(status().isUnauthorized()); // Espera um HTTP 401 Unauthorized
     * }
     */

    @Test
    @DisplayName("Deve barrar requisição quando o token for inválido")
    public void deveBarrarTokenInvalido() throws Exception {
        // Simulando que quando o filtro tentar verificar o token "token-falso", ele vai
        // estourar erro
        Mockito.when(firebaseService.verify("token-falso"))
                .thenThrow(new RuntimeException("Token inválido"));

        mockMvc.perform(post("/api/auth/login")
                .header("Authorization", "Bearer token-falso"))
                .andExpect(status().isUnauthorized());
    }
}