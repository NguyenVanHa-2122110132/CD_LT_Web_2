package com.example.lt_web2.controller;

import com.example.lt_web2.dto.ChatRequest;
import com.example.lt_web2.dto.ChatResponse;
import com.example.lt_web2.dto.StyleSuggestionResponse;
import com.example.lt_web2.dto.SummaryResponse;
import com.example.lt_web2.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    // FR-AI-001: Chatbot tư vấn sản phẩm
    @PostMapping("/product-inquiry")
    public String productInquiry(@RequestBody String customerMessage) {
        return aiService.answerProductInquiry(customerMessage);
    }

    // FR-AI-002: Gợi ý phối đồ
    @GetMapping("/style-suggestion/{variantId}")
    public List<StyleSuggestionResponse> suggestStyle(@PathVariable Integer variantId) {
        return aiService.suggestStyle(variantId);
    }

    // FR-AI-003 + FR-AI-004: Nhận tin nhắn khách, phân loại ý định + tự trả lời
    // ngoài giờ
    @PostMapping("/chat")
    public ChatResponse handleChat(@RequestBody ChatRequest req) {
        return aiService.handleIncomingMessage(req);
    }

    // FR-AI-005: Tóm tắt hội thoại
    @GetMapping("/summary/{conversationId}")
    public SummaryResponse summarizeConversation(@PathVariable Integer conversationId) {
        return aiService.summarizeConversation(conversationId);
    }
}