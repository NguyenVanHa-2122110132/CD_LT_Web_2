package com.example.lt_web2.dto;

import java.util.List;

public class OrderCreateRequest {
    private Integer customerId; // null nếu khách vãng lai
    private Integer employeeId; // thu ngân đang đăng nhập
    private List<OrderItemRequest> items;
    private String voucherCode; // null nếu không dùng voucher
    private String paymentMethod; // CASH hoặc QR

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}