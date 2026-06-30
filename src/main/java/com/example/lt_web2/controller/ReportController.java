package com.example.lt_web2.controller;

import com.example.lt_web2.dto.*;
import com.example.lt_web2.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // FR-REP-001: Thống kê doanh thu
    @PostMapping("/revenue")
    public RevenueReportResponse getRevenueReport(@RequestBody RevenueReportRequest req) {
        return reportService.getRevenueReport(req);
    }

    // FR-REP-002: Báo cáo lợi nhuận
    @PostMapping("/profit")
    public ProfitReportResponse getProfitReport(@RequestBody ProfitReportRequest req) {
        return reportService.getProfitReport(req);
    }

    // FR-REP-003: Dự báo tồn kho
    @GetMapping("/inventory-forecast")
    public List<InventoryForecastResponse> getInventoryForecast() {
        return reportService.getInventoryForecast();
    }

    // FR-REP-004: Báo cáo nhân viên (KPI dạng JSON)
    @PostMapping("/employee-kpi")
    public List<EmployeeKpiResponse> getEmployeeKpi(@RequestBody EmployeeReportRequest req) {
        return reportService.getEmployeeKpiReport(req);
    }

    // FR-REP-004: Xuất Excel
    @PostMapping("/employee-kpi/export")
    public ResponseEntity<byte[]> exportEmployeeKpi(@RequestBody EmployeeReportRequest req) throws java.io.IOException {
        byte[] excelBytes = reportService.exportEmployeeKpiToExcel(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "bao-cao-kpi-nhan-vien.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
}