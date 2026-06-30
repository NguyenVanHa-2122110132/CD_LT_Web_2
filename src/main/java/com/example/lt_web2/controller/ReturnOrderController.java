package com.example.lt_web2.controller;

import com.example.lt_web2.dto.ReturnOrderCreateRequest;
import com.example.lt_web2.dto.ReturnOrderResponse;
import com.example.lt_web2.service.ReturnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/return-orders")
public class ReturnOrderController {

    @Autowired
    private ReturnOrderService returnOrderService;

    // FR-RET-001 + FR-RET-002 + FR-RET-003: Tạo phiếu đổi trả (toàn bộ luồng)
    @PostMapping("/create")
    public ReturnOrderResponse createReturnOrder(@RequestBody ReturnOrderCreateRequest req) {
        return returnOrderService.createReturnOrder(req);
    }
}