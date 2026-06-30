package com.example.lt_web2.controller;

import com.example.lt_web2.dto.ShiftRequest;
import com.example.lt_web2.dto.ShiftResponse;
import com.example.lt_web2.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    // FR-SHF-001: Tạo ca làm việc mới
    @PostMapping("/create")
    public ShiftResponse createShift(@RequestBody ShiftRequest req) {
        return shiftService.createShift(req);
    }
}