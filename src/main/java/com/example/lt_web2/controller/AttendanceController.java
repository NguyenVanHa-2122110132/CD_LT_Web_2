package com.example.lt_web2.controller;

import com.example.lt_web2.dto.AttendanceRequest;
import com.example.lt_web2.dto.AttendanceResponse;
import com.example.lt_web2.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // FR-SHF-003: Chấm công đầu/cuối ca
    @PostMapping("/check")
    public AttendanceResponse checkInOrOut(@RequestBody AttendanceRequest req) {
        return attendanceService.checkInOrOut(req);
    }
}