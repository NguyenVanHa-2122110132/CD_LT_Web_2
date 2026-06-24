package com.example.lt_web2.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Integer id;
    private String orderCode;
    private String customerName;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount; // totalAmount - discountAmount
    private String status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public OrderResponse(Integer id, String orderCode, String customerName, BigDecimal totalAmount,
            BigDecimal discountAmount, BigDecimal finalAmount, String status,
            String paymentMethod, LocalDateTime createdAt, List<OrderItemResponse> items) {
        this.id = id;
        this.orderCode = orderCode;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Integer getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}