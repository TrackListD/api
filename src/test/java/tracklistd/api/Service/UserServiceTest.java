package tracklistd.api.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Report;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Repository.UserRepository;
import tracklistd.api.Dto.User.UserRegisterRequestDTO;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private UserService userService;

    private User adminUser;
    private User regularUser;
    private Report fakeReport;

    @BeforeEach
    void setUp(){
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);

        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setRole(Role.MEMBER);

        fakeReport = new Report();
    }

    @Test
    void shouldModerateReportSuccessfullyWhenUserIsAdmin() {
        // Arrange: configurando os mocks para não tocar em dados reais
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(reportService.resolveReport(100L, ReportStatus.RESOLVED, Punishment.WARNING)).thenReturn(fakeReport);

        // Act: execução da regra de negócio
        Report result = userService.moderateReport(1L, 100L, ReportStatus.RESOLVED, Punishment.WARNING);

        // Assert: validação do resultado e verificação do comportamento esperado
        assertNotNull(result);
        verify(reportService, times(1)).resolveReport(100L, ReportStatus.RESOLVED, Punishment.WARNING);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAdmin() {
        // Arrange: cenário de estado inválido
        when(userRepository.findById(2L)).thenReturn(Optional.of(regularUser));

        // Act & Assert: validação de exceção esperada
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.moderateReport(2L, 100L, ReportStatus.RESOLVED, Punishment.WARNING);
        });

        assertEquals("Acesso negado: apenas administradores podem moderar denúncias.", exception.getMessage());
        // Garante que o ReportService nunca foi chamado
        verify(reportService, never()).resolveReport(any(), any(), any());
    }

    @Test
    void shouldThrowExceptionWhenAdminIsNotFound() {
        // Arrange: simulando um banco de dados que não encontra o usuário
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.moderateReport(99L, 100L, ReportStatus.RESOLVED, Punishment.WARNING);
        });

        assertEquals("Administrador não encontrado.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRegisteringUserWithExistingIdLoginApi() {
        // Arrange: simulando que o repositório já encontrou o identificador no banco
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("Nome", "uid_duplicado", Role.MEMBER, Privacy.PUBLIC, "Bio");

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
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("Nome", "uid_novo", Role.MEMBER, Privacy.PUBLIC, "Bio");

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
