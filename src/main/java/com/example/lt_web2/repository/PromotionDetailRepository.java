package com.example.lt_web2.repository;

import com.example.lt_web2.entity.PromotionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Integer> {

        // Tìm khuyến mãi đang active áp dụng cho 1 variant cụ thể
        @Query("SELECT pd FROM PromotionDetail pd " +
                        "WHERE pd.productVariant.id = :variantId " +
                        "AND pd.isDeleted = false " +
                        "AND pd.promotion.status = 'ACTIVE' " +
                        "AND pd.promotion.startDate <= :now AND pd.promotion.endDate >= :now")
        Optional<PromotionDetail> findActivePromotionForVariant(@Param("variantId") Integer variantId,
                        @Param("now") LocalDateTime now);

        // FR-PRM-001: Kiểm tra SKU này có đang nằm trong chiến dịch khác đang ACTIVE và
        // trùng thời gian không

        @Query("SELECT COUNT(pd) > 0 FROM PromotionDetail pd " +
                        "WHERE pd.productVariant.id = :variantId AND pd.isDeleted = false " +
                        "AND pd.promotion.status = 'ACTIVE' " +
                        "AND pd.promotion.startDate < :endDate AND pd.promotion.endDate > :startDate")
        boolean existsOverlappingPromotion(@Param("variantId") Integer variantId,
                        @Param("startDate") java.time.LocalDateTime startDate,
                        @Param("endDate") java.time.LocalDateTime endDate);
}
