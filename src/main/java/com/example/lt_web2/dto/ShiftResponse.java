package com.example.lt_web2.dto;

import java.time.LocalTime;

public class ShiftResponse {
    private Integer id;
    private String shiftCode;
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double workingHours;

    public ShiftResponse(Integer id, String shiftCode, String shiftName,
            LocalTime startTime, LocalTime endTime, Double workingHours) {
        this.id = id;
        this.shiftCode = shiftCode;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workingHours = workingHours;
    }

    public Integer getId() {
        return id;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public String getShiftName() {
        return shiftName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Double getWorkingHours() {
        return workingHours;
    }
}