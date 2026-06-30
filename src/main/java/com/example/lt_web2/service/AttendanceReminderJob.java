package com.example.lt_web2.service;

import com.example.lt_web2.entity.Attendance;
import com.example.lt_web2.entity.Employee;
import com.example.lt_web2.entity.Shift;
import com.example.lt_web2.entity.ShiftAssignment;
import com.example.lt_web2.repository.AttendanceRepository;
import com.example.lt_web2.repository.ShiftAssignmentRepository;
import com.example.lt_web2.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceReminderJob {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private ShiftAssignmentRepository shiftAssignmentRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private NotificationService notificationService;

    // Chạy mỗi phút để kiểm tra xem có ca nào vừa qua đúng 15 phút kể từ giờ bắt
    // đầu
    @Scheduled(cron = "0 * * * * *")
    public void checkMissedCheckIns() {

        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        LocalDate today = LocalDate.now();

        List<Shift> allShifts = shiftRepository.findAll();

        for (Shift shift : allShifts) {
            LocalTime reminderTime = shift.getStartTime().plusMinutes(15);

            // Chỉ xử lý đúng phút thứ 15 sau giờ bắt đầu ca
            if (!reminderTime.equals(now))
                continue;

            List<ShiftAssignment> assignments = shiftAssignmentRepository
                    .findByShiftIdAndWorkingDate(shift.getId(), today);

            for (ShiftAssignment assignment : assignments) {
                Employee employee = assignment.getEmployee();

                List<Attendance> checkIns = attendanceRepository
                        .findCheckInByEmployeeAndDate(employee.getId(), today);

                if (checkIns.isEmpty()) {
                    String message = "Bạn có lịch trực ca \"" + shift.getShiftName() +
                            "\" bắt đầu lúc " + shift.getStartTime() +
                            " nhưng hệ thống chưa ghi nhận check-in. Vui lòng chấm công ngay!";
                    notificationService.sendReminderToEmployee(employee.getFullName(),
                            employee.getPhoneNumber(), message);
                }
            }
        }
    }
}