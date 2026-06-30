package com.example.lt_web2.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PromotionCreateRequest {
    private String promotionName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<PromotionItemRequest> items;

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<PromotionItemRequest> getItems() {
        return items;
    }

    public void setItems(List<PromotionItemRequest> items) {
        this.items = items;
    }
}