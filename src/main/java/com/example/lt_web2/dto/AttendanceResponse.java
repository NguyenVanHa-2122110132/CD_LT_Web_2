package com.example.lt_web2.dto;

import java.time.LocalDateTime;

public class AttendanceResponse {
    private String employeeName;
    private String checkType;
    private LocalDateTime checkTime;

    public AttendanceResponse(String employeeName, String checkType, LocalDateTime checkTime) {
        this.employeeName = employeeName;
        this.checkType = checkType;
        this.checkTime = checkTime;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getCheckType() {
        return checkType;
    }

    public LocalDateTime getCheckTime() {
        return checkTime;
    }
}