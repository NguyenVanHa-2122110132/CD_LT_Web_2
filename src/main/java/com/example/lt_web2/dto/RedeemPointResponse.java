package com.example.lt_web2.dto;

import java.math.BigDecimal;

public class RedeemPointResponse {
    private BigDecimal discountAmount;
    private Integer remainingPoints;

    public RedeemPointResponse(BigDecimal discountAmount, Integer remainingPoints) {
        this.discountAmount = discountAmount;
        this.remainingPoints = remainingPoints;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public Integer getRemainingPoints() {
        return remainingPoints;
    }
}