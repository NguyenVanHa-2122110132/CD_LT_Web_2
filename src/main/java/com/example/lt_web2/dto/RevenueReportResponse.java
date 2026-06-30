package com.example.lt_web2.dto;

import java.math.BigDecimal;
import java.util.List;

public class RevenueReportResponse {
    private BigDecimal totalRevenue;
    private Long totalSuccessOrders;
    private List<TopProductResponse> topProducts;

    public RevenueReportResponse(BigDecimal totalRevenue, Long totalSuccessOrders,
            List<TopProductResponse> topProducts) {
        this.totalRevenue = totalRevenue;
        this.totalSuccessOrders = totalSuccessOrders;
        this.topProducts = topProducts;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public Long getTotalSuccessOrders() {
        return totalSuccessOrders;
    }

    public List<TopProductResponse> getTopProducts() {
        return topProducts;
    }
}