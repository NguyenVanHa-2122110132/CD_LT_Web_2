package com.example.lt_web2.dto;

public class SummaryResponse {
    private String summary; // đúng 3 dòng: khách muốn gì, đã tư vấn gì, hành động tiếp theo

    public SummaryResponse(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }
}