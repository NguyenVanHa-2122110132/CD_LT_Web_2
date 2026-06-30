package com.example.lt_web2.controller;

import com.example.lt_web2.dto.ComboPolicyRequest;
import com.example.lt_web2.dto.ComboPolicyResponse;
import com.example.lt_web2.service.ComboPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/combo-policies")
public class ComboPolicyController {

    @Autowired
    private ComboPolicyService comboPolicyService;

    // FR-PRM-003: Thiết lập Combo
    @PostMapping("/create")
    public ComboPolicyResponse createCombo(@RequestBody ComboPolicyRequest req) {
        return comboPolicyService.createCombo(req);
    }
}