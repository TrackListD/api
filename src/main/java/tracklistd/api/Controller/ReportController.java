package tracklistd.api.Controller;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<ReportResponseDto> createReport(@RequestBody ReportRequestDTO reportDto) {
        Report newReport = reportService.createReport(reportDto);
        ReportResponseDto response = new ReportResponseDto(newReport);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<ReportResponseDto>> getPendingReports() {
    
        List<ReportResponseDto> pendingReports = reportService.getPendingReportsDto();
        return ResponseEntity.ok(pendingReports);
    }

    
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{reportId}/resolve")
    public ResponseEntity<ReportResponseDto> resolveReport(@PathVariable Long reportId, 
        @RequestParam ReportStatus newStatus, 
        @RequestParam(required = false) Punishment punishment) {

        ReportResponseDto response = reportService.resolveReport(reportId, newStatus, punishment); 
    
        return ResponseEntity.ok(response);
    }


    @GetMapping("/history/{userId}")
    public ResponseEntity <List<ReportResponseDto>> getReportHistoryAgainstUser(@PathVariable Long userId) {
        List<ReportResponseDto> reports = reportService.getReportHistoryUser(userId);
        
        return ResponseEntity.ok(reports);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending/user/{userId}")
    public ResponseEntity <List<ReportResponseDto>> getPendingReportsAgainstUser(@PathVariable Long userId){
        List<ReportResponseDto> list = reportService.getPendingReportAgainstUser(userId);

        return ResponseEntity.ok(list);
    }

}
