package com.example.lt_web2.dto;

public class StyleSuggestionResponse {
    private String skuCode;
    private String productName;
    private String reason;

    public StyleSuggestionResponse(String skuCode, String productName, String reason) {
        this.skuCode = skuCode;
        this.productName = productName;
        this.reason = reason;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getReason() {
        return reason;
    }
}