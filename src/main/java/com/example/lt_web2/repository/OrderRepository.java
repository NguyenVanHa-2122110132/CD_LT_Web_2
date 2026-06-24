package com.example.lt_web2.repository;

import com.example.lt_web2.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(Integer customerId);

    // Đếm số đơn hàng đã tạo trong ngày hôm nay, dùng để sinh STT cho mã hóa đơn
    // HD+YYYYMMDD+STT
    @Query(value = "SELECT COUNT(*) FROM orders WHERE CONVERT(date, created_at) = CONVERT(date, GETDATE())", nativeQuery = true)
    long countOrdersToday();
}