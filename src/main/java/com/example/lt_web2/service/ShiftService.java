package com.example.lt_web2.service;

import com.example.lt_web2.dto.ShiftRequest;
import com.example.lt_web2.dto.ShiftResponse;
import com.example.lt_web2.entity.Shift;
import com.example.lt_web2.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    // ====== FR-SHF-001: Cấu hình ca làm việc mới ======
    public ShiftResponse createShift(ShiftRequest req) {

        if (req.getShiftCode() == null || req.getShiftCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã ca làm việc không được để trống.");
        }
        if (shiftRepository.existsByShiftCode(req.getShiftCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã ca làm việc đã tồn tại.");
        }
        if (req.getShiftName() == null || req.getShiftName().length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên ca không hợp lệ (tối đa 50 ký tự).");
        }
        if (req.getWorkingHours() == null || req.getWorkingHours() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số giờ định mức phải là số dương > 0.");
        }

        LocalTime start, end;
        try {
            start = LocalTime.parse(req.getStartTime(), TIME_FORMAT);
            end = LocalTime.parse(req.getEndTime(), TIME_FORMAT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Giờ bắt đầu/kết thúc phải đúng định dạng HH:mm.");
        }

        if (!end.isAfter(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giờ kết thúc phải lớn hơn giờ bắt đầu.");
        }

        Shift shift = new Shift();
        shift.setShiftCode(req.getShiftCode().toUpperCase());
        shift.setShiftName(req.getShiftName());
        shift.setStartTime(start);
        shift.setEndTime(end);
        shift.setWorkingHours(req.getWorkingHours());

        shift = shiftRepository.save(shift);

        return new ShiftResponse(shift.getId(), shift.getShiftCode(), shift.getShiftName(),
                shift.getStartTime(), shift.getEndTime(), shift.getWorkingHours());
    }
}