package com.example.lt_web2.dto;

import java.time.LocalDateTime;

public class PromotionResponse {
    private Integer id;
    private String promotionName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private Integer itemCount;

    public PromotionResponse(Integer id, String promotionName, LocalDateTime startDate,
            LocalDateTime endDate, String status, Integer itemCount) {
        this.id = id;
        this.promotionName = promotionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.itemCount = itemCount;
    }

    public Integer getId() {
        return id;
    }

    public String getPromotionName() {
        return promotionName;
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

    public Integer getItemCount() {
        return itemCount;
    }
}