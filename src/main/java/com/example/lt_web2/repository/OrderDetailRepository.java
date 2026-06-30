package com.example.lt_web2.repository;

import com.example.lt_web2.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
        List<OrderDetail> findByOrderId(Integer orderId);

        // FR-REP-001: Top sản phẩm bán chạy nhất trong khoảng thời gian
        @Query("SELECT od.productVariant.product.name, od.productVariant.skuCode, SUM(od.quantity) " +
                        "FROM OrderDetail od " +
                        "WHERE od.order.status = 'COMPLETED' AND od.order.isDeleted = false " +
                        "AND od.order.createdAt BETWEEN :start AND :end " +
                        "AND (:branchId IS NULL OR od.order.branch.id = :branchId) " +
                        "GROUP BY od.productVariant.product.name, od.productVariant.skuCode " +
                        "ORDER BY SUM(od.quantity) DESC")
        List<Object[]> findTopSellingProducts(@Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("branchId") Integer branchId,
                        Pageable pageable);

        // FR-REP-002: Doanh thu + giá vốn theo từng danh mục sản phẩm
        @Query("SELECT od.productVariant.product.category.name, " +
                        "SUM(od.price * od.quantity), SUM(od.productVariant.importPrice * od.quantity) " +
                        "FROM OrderDetail od " +
                        "WHERE od.order.status = 'COMPLETED' AND od.order.isDeleted = false " +
                        "AND od.order.createdAt BETWEEN :start AND :end " +
                        "AND (:branchId IS NULL OR od.order.branch.id = :branchId) " +
                        "GROUP BY od.productVariant.product.category.name")
        List<Object[]> findRevenueCostByCategory(@Param("start") java.time.LocalDateTime start,
                        @Param("end") java.time.LocalDateTime end,
                        @Param("branchId") Integer branchId);

        // FR-REP-002: Tổng giá vốn toàn bộ đơn hàng trong khoảng thời gian
        @Query("SELECT COALESCE(SUM(od.productVariant.importPrice * od.quantity), 0) " +
                        "FROM OrderDetail od " +
                        "WHERE od.order.status = 'COMPLETED' AND od.order.isDeleted = false " +
                        "AND od.order.createdAt BETWEEN :start AND :end " +
                        "AND (:branchId IS NULL OR od.order.branch.id = :branchId)")
        java.math.BigDecimal sumTotalCostByDateRangeAndBranch(@Param("start") java.time.LocalDateTime start,
                        @Param("end") java.time.LocalDateTime end,
                        @Param("branchId") Integer branchId);

        // FR-REP-003: Tổng số lượng bán ra của mỗi SKU trong N tháng gần nhất (dùng để
        // dự báo)
        @Query("SELECT od.productVariant.id, od.productVariant.skuCode, od.productVariant.product.name, " +
                        "od.productVariant.stockQuantity, SUM(od.quantity) " +
                        "FROM OrderDetail od " +
                        "WHERE od.order.status = 'COMPLETED' AND od.order.isDeleted = false " +
                        "AND od.order.createdAt >= :sinceDate " +
                        "GROUP BY od.productVariant.id, od.productVariant.skuCode, od.productVariant.product.name, " +
                        "od.productVariant.stockQuantity")
        List<Object[]> findSalesQuantityByVariantSince(@Param("sinceDate") java.time.LocalDateTime sinceDate);
}
