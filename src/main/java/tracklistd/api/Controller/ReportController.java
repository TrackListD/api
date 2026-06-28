package tracklistd.api.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// --- Imports de Documentação Swagger ---
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import tracklistd.api.Dto.Report.ReportRequestDTO;
import tracklistd.api.Dto.Report.ReportResponseDto;
import tracklistd.api.Entity.Report;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;
import tracklistd.api.Service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController (ReportService reportService){
        this.reportService = reportService;
    }

    @Operation(summary = "Criar denúncia", description = "Abre uma nova denúncia contra um usuário, comentário ou avaliação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Denúncia criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Falha de validação (ex: múltiplos alvos informados)",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<ReportResponseDto> createReport(@RequestBody ReportRequestDTO reportDto) {
        Report newReport = reportService.createReport(reportDto);
        ReportResponseDto response = new ReportResponseDto(newReport);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar denúncias pendentes", description = "Retorna uma lista de todas as denúncias aguardando moderação. Acesso restrito a Administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de denúncias recuperada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: Requer privilégios de ADMIN",
                    content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<ReportResponseDto>> getPendingReports() {

        List<ReportResponseDto> pendingReports = reportService.getPendingReports()
                .stream()
                .map(ReportResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pendingReports);
    }

    @Operation(summary = "Resolver denúncia", description = "Aplica o veredito a uma denúncia (ex: RESOLVED) e executa a punição em cascata. Acesso restrito a Administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Denúncia moderada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: Requer privilégios de ADMIN",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Falha: Denúncia não encontrada no sistema",
                    content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{reportId}/resolve")
    public ResponseEntity<ReportResponseDto> resolveReport(@PathVariable Long reportId,
                                                           @RequestParam ReportStatus newStatus,
                                                           @RequestParam(required = false) Punishment punishment) {

        Report report = reportService.resolveReport(reportId, newStatus, punishment);
        return ResponseEntity.ok(new ReportResponseDto(report));
    }

    @Operation(summary = "Buscar histórico de um usuário", description = "Consulta todas as denúncias que já foram abertas contra um ID de usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico recuperado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportResponseDto.class)))
    })
    @GetMapping("/history/{userId}")
    public ResponseEntity <List<ReportResponseDto>> getReportHistoryAgainstUser(@PathVariable Long userId) {

        List<ReportResponseDto> reports = reportService.getReportHistoryUser(userId)
                .stream()
                .map(ReportResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Denúncias pendentes de um usuário", description = "Retorna apenas as denúncias não resolvidas contra um usuário específico. Acesso restrito a Administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: Requer privilégios de ADMIN",
                    content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending/user/{userId}")
    public ResponseEntity <List<ReportResponseDto>> getPendingReportsAgainstUser(@PathVariable Long userId){

        List<ReportResponseDto> list = reportService.getPendingReportAgainstUser(userId)
                .stream()
                .map(ReportResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}