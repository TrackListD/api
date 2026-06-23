package tracklistd.api.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import tracklistd.api.Dto.Admin.AdminModerateReportRequestDTO;
import tracklistd.api.Entity.Report;
import tracklistd.api.Service.AdminService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Moderar uma denúncia", description = "Permite que um administrador defina o status final e aplique punições.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Denúncia moderada e devidas providências tomadas."),
            @ApiResponse(responseCode = "404", description = "Denúncia não encontrada no banco."),
            @ApiResponse(responseCode = "403", description = "Acesso bloqueado: Credenciais de administrador ausentes.")
    })
    @PatchMapping("/reports/{reportId}/moderate")
    public ResponseEntity<Report> moderateReport(
            @PathVariable Long reportId,
            @Valid @RequestBody AdminModerateReportRequestDTO dto) {

        Report moderatedReport = adminService.moderateReport(
                reportId,
                dto.status(),
                dto.punishment(),
                dto.daysOfSuspension()
        );

        return ResponseEntity.ok(moderatedReport);
    }
}