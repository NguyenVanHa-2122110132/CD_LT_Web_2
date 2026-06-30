package com.example.lt_web2.dto;

import java.time.LocalDate;

public class ShiftAssignmentResponse {
    private Integer id;
    private String employeeName;
    private String shiftName;
    private LocalDate workingDate;

    public ShiftAssignmentResponse(Integer id, String employeeName, String shiftName, LocalDate workingDate) {
        this.id = id;
        this.employeeName = employeeName;
        this.shiftName = shiftName;
        this.workingDate = workingDate;
    }

    public Integer getId() {
        return id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getShiftName() {
        return shiftName;
    }

    public LocalDate getWorkingDate() {
        return workingDate;
    }
}