package tracklistd.api.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import tracklistd.api.Dto.Report.ReportRequestDTO;
import tracklistd.api.Dto.Report.ReportResponseDto;
import tracklistd.api.Entity.Report;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Enums.ReportStatus;
import tracklistd.api.Exceptions.ReportExceptions.ReportDoesNotExist;
import tracklistd.api.Repository.ReportRepository;
import tracklistd.api.Service.ReportService;

@RestController
@RequestMapping("/api/Entity/Report")
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

    public ResponseEntity<List<ReportResponseDto>> getPendingReports() {
    
        List<ReportResponseDto> pendingReports = reportService.getPendingReportsDto();
        return ResponseEntity.ok(pendingReports);
    }

    @Transactional
    public ReportResponseDto resolveReport(Long reportId, ReportStatus newStatus, Punishment punishment) {

        Report report = reportService.
    
        report.solveReport(newStatus, punishment);
    
        return new ReportResponseDto(updatedReport);
    }

    public List<ReportResponseDto> getReportHistoryAgainstUser(Long userId) {
        List<Report> reports = reportService.getReportHistoryUser(userId);
        
        return reports.stream()
                    .map(ReportResponseDto::new)
                    .toList();
    }
}
