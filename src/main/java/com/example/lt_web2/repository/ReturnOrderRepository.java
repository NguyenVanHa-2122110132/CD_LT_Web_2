package com.example.lt_web2.repository;

import com.example.lt_web2.entity.ReturnOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@org.springframework.stereotype.Repository
public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Integer> {

    // Sinh mã phiếu đổi trả: DT + YYYYMMDD + STT
    @Query(value = "SELECT COUNT(*) FROM return_orders WHERE CONVERT(date, created_at) = CONVERT(date, GETDATE())", nativeQuery = true)
    long countReturnsToday();
}