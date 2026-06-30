package com.example.lt_web2.dto;

import java.math.BigDecimal;

public class EmployeeKpiResponse {
    private String employeeCode;
    private String fullName;
    private Long totalCompletedOrders;
    private BigDecimal totalRevenue;
    private Long totalReturnedOrders;
    private BigDecimal returnRatePercent;

    public EmployeeKpiResponse(String employeeCode, String fullName, Long totalCompletedOrders,
            BigDecimal totalRevenue, Long totalReturnedOrders) {
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.totalCompletedOrders = totalCompletedOrders;
        this.totalRevenue = totalRevenue;
        this.totalReturnedOrders = totalReturnedOrders;
        this.returnRatePercent = totalCompletedOrders > 0
                ? BigDecimal.valueOf(totalReturnedOrders)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalCompletedOrders), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public Long getTotalCompletedOrders() {
        return totalCompletedOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public Long getTotalReturnedOrders() {
        return totalReturnedOrders;
    }

    public BigDecimal getReturnRatePercent() {
        return returnRatePercent;
    }
}