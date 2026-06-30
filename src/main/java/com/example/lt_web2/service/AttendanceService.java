package com.example.lt_web2.service;

import com.example.lt_web2.dto.AttendanceRequest;
import com.example.lt_web2.dto.AttendanceResponse;
import com.example.lt_web2.entity.Attendance;
import com.example.lt_web2.entity.Employee;
import com.example.lt_web2.repository.AttendanceRepository;
import com.example.lt_web2.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    // ====== FR-SHF-003: Chấm công đầu/cuối ca ======
    public AttendanceResponse checkInOrOut(AttendanceRequest req) {

        if (req.getPinCode() == null || !req.getPinCode().matches("^\\d{6}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã PIN phải gồm đúng 6 chữ số.");
        }
        if (req.getCheckType() == null ||
                !(req.getCheckType().equals("CHECK_IN") || req.getCheckType().equals("CHECK_OUT"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại chấm công không hợp lệ.");
        }

        Employee employee = employeeRepository.findAll().stream()
                .filter(e -> req.getPinCode().equals(e.getPinCode()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Mã PIN không chính xác, vui lòng thử lại!"));

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setCheckType(req.getCheckType());
        // checkTime tự gán bởi @PrePersist trong Attendance.java, không cần set thủ
        // công

        attendance = attendanceRepository.save(attendance);

        return new AttendanceResponse(employee.getFullName(), attendance.getCheckType(), attendance.getCheckTime());
    }
}