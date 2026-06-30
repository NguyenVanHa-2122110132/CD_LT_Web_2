package com.example.lt_web2.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "return_order_details")
public class ReturnOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "return_order_id", nullable = false)
    private ReturnOrder returnOrder;

    @ManyToOne
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 255)
    private String note;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    @Column(name = "difference_amount", precision = 18, scale = 2)
    private BigDecimal differenceAmount = BigDecimal.ZERO; // dương = thu thêm, âm = hoàn lại

    @Column(name = "refund_voucher_code", length = 50)
    private String refundVoucherCode; // mã voucher hoàn tiền (nếu có)

    public BigDecimal getDifferenceAmount() {
        return differenceAmount;
    }

    public void setDifferenceAmount(BigDecimal differenceAmount) {
        this.differenceAmount = differenceAmount;
    }

    public String getRefundVoucherCode() {
        return refundVoucherCode;
    }

    public void setRefundVoucherCode(String refundVoucherCode) {
        this.refundVoucherCode = refundVoucherCode;
    }

    // Constructor
    public ReturnOrderDetail() {
    }

    // Getter & Setter

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ReturnOrder getReturnOrder() {
        return returnOrder;
    }

    public void setReturnOrder(ReturnOrder returnOrder) {
        this.returnOrder = returnOrder;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}