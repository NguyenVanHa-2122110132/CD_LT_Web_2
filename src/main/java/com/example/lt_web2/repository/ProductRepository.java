package com.example.lt_web2.repository;

import com.example.lt_web2.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Kiểm tra trùng mã sản phẩm cha
    boolean existsByProductCode(String productCode);

    // Lấy tất cả sản phẩm chưa bị xóa
    List<Product> findByIsDeletedFalse();

    // FR-PRO-003: Tìm kiếm theo keyword (tên hoặc mã) + lọc theo danh mục/thương
    // hiệu
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:brandId IS NULL OR p.brand.id = :brandId)")
    List<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("brandId") Integer brandId);
}