package com.example.lt_web2.controller;

import com.example.lt_web2.dto.*;
import com.example.lt_web2.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Lấy danh sách toàn bộ khách hàng (dùng cho trang quản lý frontend)
    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // FR-CUS-001: Thêm nhanh khách hàng
    @PostMapping("/quick-add")
    public CustomerResponse quickAdd(@RequestBody CustomerCreateRequest req) {
        return customerService.quickAddCustomer(req);
    }

    // FR-CUS-003: Lịch sử mua hàng
    @GetMapping("/{id}/orders")
    public List<OrderHistoryResponse> getPurchaseHistory(@PathVariable Integer id) {
        return customerService.getPurchaseHistory(id);
    }

    // FR-CUS-004: Quét mã QR thành viên
    @GetMapping("/qr/{code}")
    public CustomerResponse findByQrCode(@PathVariable String code) {
        return customerService.findByQrCode(code);
    }

    // FR-CUS-005: Đổi điểm lấy giảm giá
    @PostMapping("/redeem-points")
    public RedeemPointResponse redeemPoints(@RequestBody RedeemPointRequest req) {
        return customerService.redeemPoints(req);
    }
}