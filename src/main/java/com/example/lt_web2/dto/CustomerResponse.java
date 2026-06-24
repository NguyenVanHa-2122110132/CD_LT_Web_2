package com.example.lt_web2.dto;

public class CustomerResponse {
    private Integer id;
    private String customerCode;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private Integer totalPoints;

    public CustomerResponse(Integer id, String customerCode, String fullName,
            String phoneNumber, String email, String address, Integer totalPoints) {
        this.id = id;
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.totalPoints = totalPoints;
    }

    public Integer getId() {
        return id;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }
}