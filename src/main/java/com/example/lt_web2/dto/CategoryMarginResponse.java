package com.example.lt_web2.dto;

import java.math.BigDecimal;

public class CategoryMarginResponse {
    private String categoryName;
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal grossProfit;
    private BigDecimal marginPercent;

    public CategoryMarginResponse(String categoryName, BigDecimal revenue, BigDecimal cost) {
        this.categoryName = categoryName;
        this.revenue = revenue;
        this.cost = cost;
        this.grossProfit = revenue.subtract(cost);
        this.marginPercent = revenue.compareTo(BigDecimal.ZERO) > 0
                ? grossProfit.multiply(BigDecimal.valueOf(100)).divide(revenue, 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public BigDecimal getMarginPercent() {
        return marginPercent;
    }
}