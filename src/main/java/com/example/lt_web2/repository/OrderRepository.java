package com.example.lt_web2.repository;

import com.example.lt_web2.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

        List<Order> findByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(Integer customerId);

        // Đếm số đơn hàng đã tạo trong ngày hôm nay, dùng để sinh STT cho mã hóa đơn
        // HD+YYYYMMDD+STT
        @Query(value = "SELECT COUNT(*) FROM orders WHERE CONVERT(date, created_at) = CONVERT(date, GETDATE())", nativeQuery = true)
        long countOrdersToday();

        // FR-REP-001: Tổng doanh thu thực tế (chỉ tính đơn COMPLETED, đã loại trừ
        // CANCELLED)
        @Query("SELECT COALESCE(SUM(o.totalAmount - o.discountAmount), 0) FROM Order o " +
                        "WHERE o.status = 'COMPLETED' AND o.isDeleted = false " +
                        "AND o.createdAt BETWEEN :start AND :end " +
                        "AND (:branchId IS NULL OR o.branch.id = :branchId)")
        BigDecimal sumRevenueByDateRangeAndBranch(@Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("branchId") Integer branchId);

        // FR-REP-001: Tổng số đơn hàng thành công
        @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'COMPLETED' AND o.isDeleted = false " +
                        "AND o.createdAt BETWEEN :start AND :end " +
                        "AND (:branchId IS NULL OR o.branch.id = :branchId)")
        Long countSuccessOrdersByDateRangeAndBranch(@Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("branchId") Integer branchId);

        // FR-REP-004: KPI nhân viên - gom nhóm theo employee trong tháng/năm
        @Query("SELECT o.employee.employeeCode, o.employee.fullName, COUNT(o), " +
                        "COALESCE(SUM(o.totalAmount - o.discountAmount), 0) " +
                        "FROM Order o " +
                        "WHERE o.status = 'COMPLETED' AND o.isDeleted = false AND o.employee IS NOT NULL " +
                        "AND MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year " +
                        "GROUP BY o.employee.employeeCode, o.employee.fullName")
        List<Object[]> findEmployeeKpiByMonth(@Param("month") Integer month, @Param("year") Integer year);
}