package tracklistd.api.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Repository.UserRepository;
import tracklistd.api.Dto.User.UserRegisterRequestDTO;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);

        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setRole(Role.MEMBER);
    }

    @Test
    void shouldThrowExceptionWhenRegisteringUserWithExistingIdLoginApi() {
        // Arrange: simulando que o repositório já encontrou o identificador no banco
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("Nome", "uid_duplicado", Role.MEMBER, Privacy.PUBLIC,
                "Bio", "imagem-teste");

        when(userRepository.existsByIdLoginApi("uid_duplicado")).thenReturn(true);
        // Act & Assert: garantindo que a exceção correta seja lançada
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(dto);
        });

        assertEquals("Usuário já está cadastrado no sistema!", exception.getMessage());
        // Verificando que a operação de salvamento nunca foi alcançada
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldRegisterUserSuccessfullyWhenIdLoginApiIsNew() {
        // Arrange: simulando que o identificador está livre para uso
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("Nome", "uid_novo", Role.MEMBER, Privacy.PUBLIC, "Bio",
                "imagem-teste");

        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setIdLoginApi("uid_novo");

        when(userRepository.existsByIdLoginApi("uid_novo")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act: execução do método de registro
        User result = userService.register(dto);

        // Assert: conferindo o sucesso e a comunicação correta com o repositório
        assertNotNull(result);
        assertEquals("uid_novo", result.getIdLoginApi());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
