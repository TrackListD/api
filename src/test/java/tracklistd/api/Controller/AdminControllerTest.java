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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tracklistd.api.Mapper.ReportMapper;
import tracklistd.api.Dto.Report.ReportResponseDto;
import tracklistd.api.Dto.Admin.AdminModerateReportRequestDTO;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;
import tracklistd.api.Entity.Report;
import tracklistd.api.Entity.User;
import tracklistd.api.Integration.FirebaseAuth.Config.SecurityConfig;
import tracklistd.api.Integration.FirebaseAuth.FirebaseFilter;
import tracklistd.api.Service.AdminService;
import tracklistd.api.Service.FirebaseService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, FirebaseFilter.class})
@EnableMethodSecurity
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private FirebaseService firebaseService;

    @MockitoBean
    private tracklistd.api.Service.UserService userService;

    @MockitoBean
    private tracklistd.api.Service.ReportService reportService;

    @MockitoBean
    private ReportMapper reportMapper;

    private UsernamePasswordAuthenticationToken adminAuth;
    private UsernamePasswordAuthenticationToken userAuth;
    private Report testReport;

    @BeforeEach
    void setUp() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setName("Admin");

        User normalUser = new User();
        normalUser.setId(2L);
        normalUser.setName("Normal");

        // Autenticação mockada com perfil de Administrador
        adminAuth = new UsernamePasswordAuthenticationToken(
                adminUser, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // Autenticação mockada com perfil Comum (Membro)
        userAuth = new UsernamePasswordAuthenticationToken(
                normalUser, null, List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));

        testReport = new Report(adminUser, "Motivo", LocalDateTime.now(), normalUser);
        testReport.solveReport(ReportStatus.RESOLVED, Punishment.TEMPORARY_SUSPENSION);
    }

    @Test
    @DisplayName("moderateReport deve retornar 200 quando administrador moderar")
    void moderateReport_deveRetornar200_quandoAdmin() throws Exception {
        AdminModerateReportRequestDTO request = new AdminModerateReportRequestDTO(
                ReportStatus.RESOLVED, Punishment.TEMPORARY_SUSPENSION, 7L
        );

        when(adminService.moderateReport(eq(10L), eq(ReportStatus.RESOLVED), eq(Punishment.TEMPORARY_SUSPENSION), eq(7L)))
                .thenReturn(testReport);

        ReportResponseDto responseDto = new ReportResponseDto(10L, 1L, 2L, "Motivo", LocalDateTime.now(), ReportStatus.RESOLVED, null, null);
        when(reportMapper.toDto(any())).thenReturn(responseDto);

        mockMvc.perform(patch("/api/admin/reports/10/moderate")
                        .with(authentication(adminAuth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportStatus").value("RESOLVED"));
    }

    @Test
    @DisplayName("moderateReport deve retornar 403 quando usuário comum tentar acessar")
    void moderateReport_deveRetornar403_quandoUsuarioComum() throws Exception {
        AdminModerateReportRequestDTO request = new AdminModerateReportRequestDTO(
                ReportStatus.RESOLVED, Punishment.TEMPORARY_SUSPENSION, 7L
        );

        mockMvc.perform(patch("/api/admin/reports/10/moderate")
                        .with(authentication(userAuth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}