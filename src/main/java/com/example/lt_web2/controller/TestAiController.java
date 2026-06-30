package com.example.lt_web2.controller;

import com.example.lt_web2.service.GroqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-ai")
public class TestAiController {

    @Autowired
    private GroqService groqService;

    @GetMapping("/ping")
    public String ping() {
        return groqService.chat("Bạn là trợ lý AI thân thiện.", "Chào bạn, bạn là ai?");
    }
}