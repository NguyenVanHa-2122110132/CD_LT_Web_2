package com.example.lt_web2.repository;

import com.example.lt_web2.entity.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Integer> {

    // Kiểm tra trùng lịch: nhân viên đã có ca nào vào đúng ngày + ca đó chưa
    boolean existsByEmployeeIdAndShiftIdAndWorkingDate(Integer employeeId, Integer shiftId, LocalDate workingDate);

    // FR-SHF-004: lấy danh sách nhân viên có lịch trực 1 ca cụ thể vào 1 ngày cụ
    // thể
    @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.shift.id = :shiftId AND sa.workingDate = :date AND sa.isDeleted = false")
    List<ShiftAssignment> findByShiftIdAndWorkingDate(@Param("shiftId") Integer shiftId, @Param("date") LocalDate date);

    List<ShiftAssignment> findByEmployeeIdAndWorkingDateBetween(Integer employeeId, LocalDate start, LocalDate end);
}