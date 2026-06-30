package com.example.lt_web2.service;

import com.example.lt_web2.dto.ShiftAssignmentRequest;
import com.example.lt_web2.dto.ShiftAssignmentResponse;
import com.example.lt_web2.entity.Employee;
import com.example.lt_web2.entity.Shift;
import com.example.lt_web2.entity.ShiftAssignment;
import com.example.lt_web2.repository.EmployeeRepository;
import com.example.lt_web2.repository.ShiftAssignmentRepository;
import com.example.lt_web2.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShiftAssignmentService {

    @Autowired
    private ShiftAssignmentRepository shiftAssignmentRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ShiftRepository shiftRepository;

    // ====== FR-SHF-002: Xếp lịch ca ======
    public ShiftAssignmentResponse assignShift(ShiftAssignmentRequest req) {

        Employee employee = employeeRepository.findById(req.getEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên"));

        Shift shift = shiftRepository.findById(req.getShiftId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ca làm việc"));

        boolean isDuplicate = shiftAssignmentRepository.existsByEmployeeIdAndShiftIdAndWorkingDate(
                req.getEmployeeId(), req.getShiftId(), req.getWorkingDate());
        if (isDuplicate) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nhân viên này đã được xếp ca này vào ngày đó rồi.");
        }

        ShiftAssignment assignment = new ShiftAssignment();
        assignment.setEmployee(employee);
        assignment.setShift(shift);
        assignment.setWorkingDate(req.getWorkingDate());

        assignment = shiftAssignmentRepository.save(assignment);

        return new ShiftAssignmentResponse(assignment.getId(), employee.getFullName(),
                shift.getShiftName(), assignment.getWorkingDate());
    }
}