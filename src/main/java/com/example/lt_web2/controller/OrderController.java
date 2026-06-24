package com.example.lt_web2.controller;

import com.example.lt_web2.dto.OrderCreateRequest;
import com.example.lt_web2.dto.OrderResponse;
import com.example.lt_web2.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // FR-ORD-001: Tạo đơn hàng tại quầy (trạng thái PENDING, đã trừ kho)
    @PostMapping("/create")
    public OrderResponse createOrder(@RequestBody OrderCreateRequest req) {
        return orderService.createOrder(req);
    }

    // FR-ORD-002: Xác nhận thanh toán & hoàn tất đơn (chuyển PENDING -> COMPLETED)
    @PostMapping("/{id}/complete")
    public OrderResponse completeOrder(@PathVariable Integer id,
            @RequestParam String paymentMethod) {
        return orderService.completeOrder(id, paymentMethod);
    }

    // FR-ORD-003: Hủy đơn hàng
    // isAdmin=true để bỏ qua giới hạn 30 phút (dành cho Admin/Manager)
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Integer id,
            @RequestParam(defaultValue = "false") boolean isAdmin) {
        orderService.cancelOrder(id, isAdmin);
        return "Đơn hàng đã được hủy thành công.";
    }
}