package com.example.lt_web2.dto;

import java.math.BigDecimal;

public class InventoryForecastResponse {
    private String skuCode;
    private String productName;
    private Integer currentStock;
    private BigDecimal avgDailySales; // tốc độ bán TB/ngày trong 3 tháng qua
    private Integer forecastNextMonth; // dự báo nhu cầu tháng tới
    private Integer suggestedRestockQty; // số lượng tối thiểu cần nhập thêm

    public InventoryForecastResponse(String skuCode, String productName, Integer currentStock,
            BigDecimal avgDailySales, Integer forecastNextMonth, Integer suggestedRestockQty) {
        this.skuCode = skuCode;
        this.productName = productName;
        this.currentStock = currentStock;
        this.avgDailySales = avgDailySales;
        this.forecastNextMonth = forecastNextMonth;
        this.suggestedRestockQty = suggestedRestockQty;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public BigDecimal getAvgDailySales() {
        return avgDailySales;
    }

    public Integer getForecastNextMonth() {
        return forecastNextMonth;
    }

    public Integer getSuggestedRestockQty() {
        return suggestedRestockQty;
    }
}