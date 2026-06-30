package com.example.lt_web2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "return_orders")
public class ReturnOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "return_code", nullable = false, unique = true)
    private String returnCode;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, length = 100)
    private String reason;

    @Column(length = 20)
    private String status;
    // PENDING, APPROVED, COMPLETED, REJECTED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "difference_amount", precision = 18, scale = 2)
    private java.math.BigDecimal differenceAmount = java.math.BigDecimal.ZERO;

    @Column(name = "refund_voucher_code", length = 50)
    private String refundVoucherCode;

    @OneToMany(mappedBy = "returnOrder", cascade = CascadeType.ALL)
    private List<ReturnOrderDetail> returnOrderDetails;

    // Constructor
    public ReturnOrder() {
    }

    // Getter & Setter

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<ReturnOrderDetail> getReturnOrderDetails() {
        return returnOrderDetails;
    }

    public void setReturnOrderDetails(List<ReturnOrderDetail> returnOrderDetails) {
        this.returnOrderDetails = returnOrderDetails;
    }

    public java.math.BigDecimal getDifferenceAmount() {
        return differenceAmount;
    }

    public void setDifferenceAmount(java.math.BigDecimal differenceAmount) {
        this.differenceAmount = differenceAmount;
    }

    public String getRefundVoucherCode() {
        return refundVoucherCode;
    }

    public void setRefundVoucherCode(String refundVoucherCode) {
        this.refundVoucherCode = refundVoucherCode;
    }
}