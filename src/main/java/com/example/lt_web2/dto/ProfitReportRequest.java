package com.example.lt_web2.dto;

import java.time.LocalDateTime;

public class ProfitReportRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer branchId;

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }
}