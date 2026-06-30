package com.example.lt_web2.controller;

import com.example.lt_web2.dto.PromotionCreateRequest;
import com.example.lt_web2.dto.PromotionResponse;
import com.example.lt_web2.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    // FR-PRM-001: Tạo chiến dịch Flash Sale
    @PostMapping("/create")
    public PromotionResponse createPromotion(@RequestBody PromotionCreateRequest req) {
        return promotionService.createPromotion(req);
    }
}