package com.example.lt_web2.repository;

import com.example.lt_web2.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    boolean existsByShiftCode(String shiftCode);
}