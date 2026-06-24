package com.example.lt_web2.repository;

import com.example.lt_web2.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

    // Kiểm tra trùng SKU
    boolean existsBySkuCode(String skuCode);

    // FR-PRO-004: Tìm biến thể theo SKU (quét mã vạch)
    Optional<ProductVariant> findBySkuCode(String skuCode);

    // Lấy tất cả biến thể của 1 sản phẩm cha
    List<ProductVariant> findByProductId(Integer productId);

    // FR-PRO-003: Tìm kiếm biến thể theo keyword (SKU, tên sản phẩm, màu, size)
    @Query("SELECT v FROM ProductVariant v JOIN v.product p WHERE " +
            "p.isDeleted = false AND " +
            "(:keyword IS NULL OR LOWER(v.skuCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:brandId IS NULL OR p.brand.id = :brandId)")
    List<ProductVariant> searchVariants(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("brandId") Integer brandId);

    // FR-PRO-005: Lấy các biến thể tồn kho dưới ngưỡng 10
    @Query("SELECT v FROM ProductVariant v WHERE v.stockQuantity < 10")
    List<ProductVariant> findLowStockVariants();
}