package com.example.lt_web2.dto;

public class AttendanceRequest {
    private String pinCode;
    private String checkType; // CHECK_IN hoặc CHECK_OUT

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }
}