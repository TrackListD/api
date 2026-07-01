package tracklistd.api.Controller;

import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import tracklistd.api.Mapper.ReportMapper;
import tracklistd.api.Dto.Report.ReportResponseDto;
import tracklistd.api.Dto.Admin.AdminModerateReportRequestDTO;
import tracklistd.api.Entity.Report;
import tracklistd.api.Service.AdminService;
import tracklistd.api.Service.ReportService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;
    private final ReportMapper reportMapper;

    public AdminController(AdminService adminService, ReportService reportService, ReportMapper reportMapper) {
        this.adminService = adminService;
        this.reportService = reportService;
        this.reportMapper = reportMapper;
    }

    @Operation(summary = "Buscar denúncia por ID", description = "Retorna os detalhes de uma denúncia específica para a tela de solução do administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Denúncia encontrada e detalhada."),
            @ApiResponse(responseCode = "404", description = "Denúncia não encontrada no banco de dados.")
    })
    @GetMapping("/reports/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ReportResponseDto> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        return ResponseEntity.ok(reportMapper.toDto(report));
    }

    @Operation(summary = "Moderar uma denúncia", description = "Permite que um administrador defina o status final e aplique punições.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Denúncia moderada e devidas providências tomadas."),
            @ApiResponse(responseCode = "404", description = "Denúncia não encontrada no banco."),
            @ApiResponse(responseCode = "403", description = "Acesso bloqueado: Credenciais de administrador ausentes.")
    })
    @PatchMapping("/reports/{id}/moderate")
    public ResponseEntity<ReportResponseDto> moderateReport(
            @PathVariable Long id,
            @Valid @RequestBody AdminModerateReportRequestDTO dto) {

        Report moderatedReport = adminService.moderateReport(
                id,
                dto.status(),
                dto.punishment(),
                dto.daysOfSuspension()
        );

        return ResponseEntity.ok(reportMapper.toDto(moderatedReport));
    }

    @Operation(summary = "Listar denúncias pendentes", description = "Retorna uma lista de todas as denúncias que aguardam julgamento.")
    @GetMapping("/reports/pending")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ReportResponseDto>> getPendingReports() {

        List<ReportResponseDto> pendingReports = reportService.getPendingReports()
                .stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pendingReports);
    }

    @Operation(summary = "Buscar histórico de um usuário", description = "Consulta todas as denúncias que já foram abertas contra um ID de usuário específico.")
    @GetMapping("/reports/user/{userId}")
    public ResponseEntity<List<ReportResponseDto>> getReportHistory(@PathVariable Long userId) {

        List<ReportResponseDto> history = reportService.getReportHistoryUser(userId)
                .stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }
}