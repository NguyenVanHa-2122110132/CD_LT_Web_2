package com.example.lt_web2.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnoreProperties({ "attendances", "shiftAssignments" })
    private Employee employee;

    @Column(name = "check_type", nullable = false, length = 20)
    private String checkType; // CHECK_IN hoặc CHECK_OUT

    @Column(name = "check_time", nullable = false)
    private LocalDateTime checkTime;

    @Column(length = 255)
    private String note;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // Constructor
    public Attendance() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.checkTime == null) {
            this.checkTime = LocalDateTime.now();
        }
    }

    // Getters & Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public LocalDateTime getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(LocalDateTime checkTime) {
        this.checkTime = checkTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}