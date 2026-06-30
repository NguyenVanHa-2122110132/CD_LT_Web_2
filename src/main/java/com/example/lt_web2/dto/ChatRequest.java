package com.example.lt_web2.dto;

public class ChatRequest {
    private Integer conversationId; // null nếu là cuộc hội thoại mới
    private Integer customerId; // null nếu khách lạ chưa có trong hệ thống
    private String message;

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}