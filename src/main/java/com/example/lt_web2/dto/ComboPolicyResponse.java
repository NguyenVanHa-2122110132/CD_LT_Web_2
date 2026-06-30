package com.example.lt_web2.dto;

import java.math.BigDecimal;

public class ComboPolicyResponse {
    private Integer id;
    private String comboName;
    private String categoryName;
    private Integer requiredQuantity;
    private BigDecimal fixedPrice;

    public ComboPolicyResponse(Integer id, String comboName, String categoryName,
            Integer requiredQuantity, BigDecimal fixedPrice) {
        this.id = id;
        this.comboName = comboName;
        this.categoryName = categoryName;
        this.requiredQuantity = requiredQuantity;
        this.fixedPrice = fixedPrice;
    }

    public Integer getId() {
        return id;
    }

    public String getComboName() {
        return comboName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Integer getRequiredQuantity() {
        return requiredQuantity;
    }

    public BigDecimal getFixedPrice() {
        return fixedPrice;
    }
}