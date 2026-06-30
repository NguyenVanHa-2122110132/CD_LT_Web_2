package com.example.lt_web2.controller;

import com.example.lt_web2.dto.ShiftAssignmentRequest;
import com.example.lt_web2.dto.ShiftAssignmentResponse;
import com.example.lt_web2.service.ShiftAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shift-assignments")
public class ShiftAssignmentController {

    @Autowired
    private ShiftAssignmentService shiftAssignmentService;

    // FR-SHF-002: Xếp lịch ca
    @PostMapping("/assign")
    public ShiftAssignmentResponse assignShift(@RequestBody ShiftAssignmentRequest req) {
        return shiftAssignmentService.assignShift(req);
    }
}