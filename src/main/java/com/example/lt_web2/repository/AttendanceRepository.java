package com.example.lt_web2.repository;

import com.example.lt_web2.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    // FR-SHF-004: kiểm tra nhân viên đã check-in trong ngày hiện tại chưa
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId " +
            "AND a.checkType = 'CHECK_IN' AND CAST(a.checkTime AS date) = :date AND a.isDeleted = false")
    List<Attendance> findCheckInByEmployeeAndDate(@Param("employeeId") Integer employeeId,
            @Param("date") LocalDate date);
}