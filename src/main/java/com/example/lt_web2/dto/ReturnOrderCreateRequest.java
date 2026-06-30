package com.example.lt_web2.dto;

import java.util.List;

public class ReturnOrderCreateRequest {
    private String originalOrderCode;
    private String reason; // "Hàng lỗi đường may" | "Khách đổi size" | "Khách đổi mẫu khác"
    private List<ReturnItemRequest> returnedItems; // món khách trả lại
    private List<ReturnItemRequest> newItems; // món khách lấy mới (có thể rỗng)

    public String getOriginalOrderCode() {
        return originalOrderCode;
    }

    public void setOriginalOrderCode(String originalOrderCode) {
        this.originalOrderCode = originalOrderCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ReturnItemRequest> getReturnedItems() {
        return returnedItems;
    }

    public void setReturnedItems(List<ReturnItemRequest> returnedItems) {
        this.returnedItems = returnedItems;
    }

    public List<ReturnItemRequest> getNewItems() {
        return newItems;
    }

    public void setNewItems(List<ReturnItemRequest> newItems) {
        this.newItems = newItems;
    }
}