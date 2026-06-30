package com.example.lt_web2.controller;

import com.example.lt_web2.dto.VoucherCreateRequest;
import com.example.lt_web2.dto.VoucherResponse;
import com.example.lt_web2.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    // FR-PRM-002: Phát hành mã Voucher
    @PostMapping("/create")
    public VoucherResponse createVoucher(@RequestBody VoucherCreateRequest req) {
        return voucherService.createVoucher(req);
    }
}