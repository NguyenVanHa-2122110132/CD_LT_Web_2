package com.example.lt_web2.service;

import com.example.lt_web2.dto.*;
import com.example.lt_web2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

        @Autowired
        private OrderRepository orderRepository;
        @Autowired
        private OrderDetailRepository orderDetailRepository;

        // ====== FR-REP-001: Thống kê doanh thu ======
        public RevenueReportResponse getRevenueReport(RevenueReportRequest req) {

                BigDecimal totalRevenue = orderRepository.sumRevenueByDateRangeAndBranch(
                                req.getStartDate(), req.getEndDate(), req.getBranchId());

                Long totalOrders = orderRepository.countSuccessOrdersByDateRangeAndBranch(
                                req.getStartDate(), req.getEndDate(), req.getBranchId());

                List<Object[]> rawTop = orderDetailRepository.findTopSellingProducts(
                                req.getStartDate(), req.getEndDate(), req.getBranchId(), PageRequest.of(0, 10));

                List<TopProductResponse> topProducts = rawTop.stream()
                                .map(row -> new TopProductResponse(
                                                (String) row[0],
                                                (String) row[1],
                                                ((Number) row[2]).longValue()))
                                .collect(Collectors.toList());

                return new RevenueReportResponse(totalRevenue, totalOrders, topProducts);
        }

        // ====== FR-REP-002: Báo cáo lợi nhuận ======
        public ProfitReportResponse getProfitReport(ProfitReportRequest req) {

                BigDecimal totalRevenue = orderRepository.sumRevenueByDateRangeAndBranch(
                                req.getStartDate(), req.getEndDate(), req.getBranchId());

                BigDecimal totalCost = orderDetailRepository.sumTotalCostByDateRangeAndBranch(
                                req.getStartDate(), req.getEndDate(), req.getBranchId());

                List<Object[]> rawCategoryData = orderDetailRepository.findRevenueCostByCategory(
                                req.getStartDate(), req.getEndDate(), req.getBranchId());

                List<CategoryMarginResponse> byCategory = rawCategoryData.stream()
                                .map(row -> new CategoryMarginResponse(
                                                (String) row[0],
                                                (BigDecimal) row[1],
                                                (BigDecimal) row[2]))
                                .collect(Collectors.toList());

                return new ProfitReportResponse(totalRevenue, totalCost, byCategory);
        }

        // ====== FR-REP-003: Dự báo tồn kho (bản đơn giản hóa - tốc độ bán trung bình)
        // ======
        public List<InventoryForecastResponse> getInventoryForecast() {

                LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
                long daysInPeriod = java.time.Duration.between(threeMonthsAgo, LocalDateTime.now()).toDays();
                if (daysInPeriod <= 0)
                        daysInPeriod = 1;

                List<Object[]> rawData = orderDetailRepository.findSalesQuantityByVariantSince(threeMonthsAgo);

                List<InventoryForecastResponse> result = new java.util.ArrayList<>();
                for (Object[] row : rawData) {
                        String skuCode = (String) row[1];
                        String productName = (String) row[2];
                        Integer currentStock = (Integer) row[3];
                        Long totalSold = ((Number) row[4]).longValue();

                        BigDecimal avgDailySales = BigDecimal.valueOf(totalSold)
                                        .divide(BigDecimal.valueOf(daysInPeriod), 2, java.math.RoundingMode.HALF_UP);

                        // Dự báo nhu cầu tháng tới = tốc độ bán TB/ngày * 30 ngày
                        int forecastNextMonth = avgDailySales.multiply(BigDecimal.valueOf(30))
                                        .setScale(0, java.math.RoundingMode.HALF_UP).intValue();

                        // Số lượng cần nhập thêm = dự báo - tồn hiện tại (nếu âm thì không cần nhập,
                        // set 0)
                        int suggestedRestock = Math.max(0, forecastNextMonth - currentStock);

                        result.add(new InventoryForecastResponse(skuCode, productName, currentStock,
                                        avgDailySales, forecastNextMonth, suggestedRestock));
                }

                // Sắp xếp theo mức độ cần nhập gấp nhất lên đầu
                result.sort((a, b) -> b.getSuggestedRestockQty().compareTo(a.getSuggestedRestockQty()));

                return result;
        }

        // ====== FR-REP-004: Báo cáo nhân viên (KPI) ======
        public List<EmployeeKpiResponse> getEmployeeKpiReport(EmployeeReportRequest req) {

                List<Object[]> rawData = orderRepository.findEmployeeKpiByMonth(req.getMonth(), req.getYear());

                return rawData.stream()
                                .map(row -> new EmployeeKpiResponse(
                                                (String) row[0],
                                                (String) row[1],
                                                ((Number) row[2]).longValue(),
                                                (BigDecimal) row[3],
                                                0L)) // tạm = 0, chưa có dữ liệu ReturnOrder
                                .collect(Collectors.toList());
        }

        // ====== FR-REP-004: Xuất Excel ======
        public byte[] exportEmployeeKpiToExcel(EmployeeReportRequest req) throws java.io.IOException {

                List<EmployeeKpiResponse> data = getEmployeeKpiReport(req);

                try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
                        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("KPI Nhan Vien");

                        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
                        String[] columns = { "Mã NV", "Họ tên", "Số đơn thành công", "Doanh thu", "Số đơn trả",
                                        "Tỷ lệ trả (%)" };
                        for (int i = 0; i < columns.length; i++) {
                                header.createCell(i).setCellValue(columns[i]);
                        }

                        int rowIdx = 1;
                        for (EmployeeKpiResponse e : data) {
                                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                                row.createCell(0).setCellValue(e.getEmployeeCode());
                                row.createCell(1).setCellValue(e.getFullName());
                                row.createCell(2).setCellValue(e.getTotalCompletedOrders());
                                row.createCell(3).setCellValue(e.getTotalRevenue().doubleValue());
                                row.createCell(4).setCellValue(e.getTotalReturnedOrders());
                                row.createCell(5).setCellValue(e.getReturnRatePercent().doubleValue());
                        }

                        for (int i = 0; i < columns.length; i++) {
                                sheet.autoSizeColumn(i);
                        }

                        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                        workbook.write(out);
                        return out.toByteArray();
                }
        }
}