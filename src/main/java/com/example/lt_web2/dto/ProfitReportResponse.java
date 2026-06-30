package com.example.lt_web2.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProfitReportResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal grossProfit;
    private BigDecimal marginPercent;
    private List<CategoryMarginResponse> byCategory;

    public ProfitReportResponse(BigDecimal totalRevenue, BigDecimal totalCost,
            List<CategoryMarginResponse> byCategory) {
        this.totalRevenue = totalRevenue;
        this.totalCost = totalCost;
        this.grossProfit = totalRevenue.subtract(totalCost);
        this.marginPercent = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? grossProfit.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        this.byCategory = byCategory;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public BigDecimal getMarginPercent() {
        return marginPercent;
    }

    public List<CategoryMarginResponse> getByCategory() {
        return byCategory;
    }
}