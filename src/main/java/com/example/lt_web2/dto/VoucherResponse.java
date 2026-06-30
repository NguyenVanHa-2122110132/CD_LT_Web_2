package com.example.lt_web2.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VoucherResponse {
    private Integer id;
    private String voucherCode;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private Integer usageLimit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;

    public VoucherResponse(Integer id, String voucherCode, String discountType, BigDecimal discountValue,
            BigDecimal minOrderValue, BigDecimal maxDiscount, Integer usageLimit,
            LocalDateTime startDate, LocalDateTime endDate, String status) {
        this.id = id;
        this.voucherCode = voucherCode;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderValue = minOrderValue;
        this.maxDiscount = maxDiscount;
        this.usageLimit = usageLimit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public String getDiscountType() {
        return discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public BigDecimal getMinOrderValue() {
        return minOrderValue;
    }

    public BigDecimal getMaxDiscount() {
        return maxDiscount;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }
}