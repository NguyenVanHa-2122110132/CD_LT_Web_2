package com.example.lt_web2.dto;

public class TopProductResponse {
    private String productName;
    private String skuCode;
    private Long quantitySold;

    public TopProductResponse(String productName, String skuCode, Long quantitySold) {
        this.productName = productName;
        this.skuCode = skuCode;
        this.quantitySold = quantitySold;
    }

    public String getProductName() {
        return productName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public Long getQuantitySold() {
        return quantitySold;
    }
}