package com.example.lt_web2.dto;

import java.math.BigDecimal;

public class ReturnOrderResponse {
    private Integer id;
    private String returnCode;
    private String reason;
    private String status;
    private BigDecimal oldItemsTotal;
    private BigDecimal newItemsTotal;
    private BigDecimal differenceAmount; // dương: khách phải trả thêm | âm: khách được hoàn
    private String refundVoucherCode; // có giá trị nếu differenceAmount âm
    private String qrPaymentNote; // có giá trị nếu differenceAmount dương

    public ReturnOrderResponse(Integer id, String returnCode, String reason, String status,
            BigDecimal oldItemsTotal, BigDecimal newItemsTotal, BigDecimal differenceAmount,
            String refundVoucherCode, String qrPaymentNote) {
        this.id = id;
        this.returnCode = returnCode;
        this.reason = reason;
        this.status = status;
        this.oldItemsTotal = oldItemsTotal;
        this.newItemsTotal = newItemsTotal;
        this.differenceAmount = differenceAmount;
        this.refundVoucherCode = refundVoucherCode;
        this.qrPaymentNote = qrPaymentNote;
    }

    public Integer getId() {
        return id;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getOldItemsTotal() {
        return oldItemsTotal;
    }

    public BigDecimal getNewItemsTotal() {
        return newItemsTotal;
    }

    public BigDecimal getDifferenceAmount() {
        return differenceAmount;
    }

    public String getRefundVoucherCode() {
        return refundVoucherCode;
    }

    public String getQrPaymentNote() {
        return qrPaymentNote;
    }
}