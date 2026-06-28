package tracklistd.api.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tracklistd.api.Dto.Report.ReportRequestDTO;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Report;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.ReportExceptions.ReportException;
import tracklistd.api.Repository.CommentRepository;
import tracklistd.api.Repository.RatingRepository;
import tracklistd.api.Repository.ReportRepository;
import tracklistd.api.Repository.UserRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private ReportService reportService;

    private User informer;
    private User target;
    private Comment commentTarget;

    @BeforeEach
    void setUp() {
        informer = new User();
        informer.setId(1L);

        target = new User();
        target.setId(2L);

        commentTarget = new Comment();
    }

    @Test
    @DisplayName("Deve criar denúncia com sucesso quando apenas UM alvo for enviado")
    void createReport_Success() {
        // Arrange
        ReportRequestDTO dto = new ReportRequestDTO(1L, 2L, "Comportamento abusivo", null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(informer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));

        // Simula o salvamento no banco e devolve o próprio objeto
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Report result = reportService.createReport(dto);

        // Assert
        assertNotNull(result);
        assertEquals(target, result.getUserTarget());
        assertNull(result.getCommentTarget());
        assertNull(result.getRatingTarget());

        // Verifica se o método save foi chamado exatamente 1 vez
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando NENHUM alvo for enviado")
    void createReport_FailNoTarget() {
        // Arrange (Deixamos todos os alvos como null)
        ReportRequestDTO dto = new ReportRequestDTO(1L, null, "Spam", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(informer));

        // Act & Assert
        ReportException exception = assertThrows(ReportException.class, () -> {
            reportService.createReport(dto);
        });

        assertEquals("A denúncia precisa ter pelo menos um alvo.", exception.getMessage());

        // Garante que a fraude foi barrada e NUNCA tentou salvar no banco
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando MÚLTIPLOS alvos forem enviados (Regra de Conflito)")
    void createReport_FailMultipleTargets() {
        // Arrange (Enviando ID de Usuário e de Comentário AO MESMO TEMPO)
        ReportRequestDTO dto = new ReportRequestDTO(1L, 2L, "Tentativa de Fraude", 10L, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(informer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(commentRepository.findById(10L)).thenReturn(Optional.of(commentTarget));

        // Act & Assert
        ReportException exception = assertThrows(ReportException.class, () -> {
            reportService.createReport(dto);
        });

        assertEquals("A denúncia não pode ter múltiplos alvos simultâneos. Envie apenas um.", exception.getMessage());

        // Garante que o banco de dados continua intocado
        verify(reportRepository, never()).save(any(Report.class));
    }
}