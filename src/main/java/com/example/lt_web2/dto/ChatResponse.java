package com.example.lt_web2.dto;

public class ChatResponse {
    private Integer conversationId;
    private String reply;
    private String tag; // WARM_LEAD, HOT_LEAD, COMPLAINT, null nếu không xác định được

    public ChatResponse(Integer conversationId, String reply, String tag) {
        this.conversationId = conversationId;
        this.reply = reply;
        this.tag = tag;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public String getReply() {
        return reply;
    }

    public String getTag() {
        return tag;
    }
}