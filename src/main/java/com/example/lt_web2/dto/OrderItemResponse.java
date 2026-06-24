package com.example.lt_web2.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
    private String skuCode;
    private String productName;
    private String color;
    private String size;
    private Integer quantity;
    private BigDecimal price;

    public OrderItemResponse(String skuCode, String productName, String color,
            String size, Integer quantity, BigDecimal price) {
        this.skuCode = skuCode;
        this.productName = productName;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
}