package com.example.lt_web2.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderHistoryResponse {
    private String orderCode;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
    private String status;

    public OrderHistoryResponse(String orderCode, LocalDateTime createdAt,
            BigDecimal totalAmount, String status) {
        this.orderCode = orderCode;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }
}