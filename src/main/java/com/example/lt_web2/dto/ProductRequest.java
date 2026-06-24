package com.example.lt_web2.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO nhận request tạo sản phẩm cha + danh sách biến thể cùng lúc
 * Dùng cho FR-PRO-001: Khởi tạo sản phẩm cha + sinh biến thể
 * trong @Transactional
 */
public class ProductRequest {

    // ===== Thông tin sản phẩm cha =====
    private String productCode; // VD: AO_POLO_01
    private String name; // Tối đa 150 ký tự
    private String description; // Không bắt buộc
    private Integer categoryId; // FK -> categories
    private Integer brandId; // FK -> brands
    private String image;

    // ===== Danh sách biến thể SKU =====
    private List<VariantItem> variants;

    public static class VariantItem {
        private String skuCode; // VD: AO_POLO_01_DO_M
        private String color;
        private String size;
        private BigDecimal importPrice;
        private BigDecimal salePrice;
        private Integer stockQuantity;
        private String barcode;

        // Getters & Setters
        public String getSkuCode() {
            return skuCode;
        }

        public void setSkuCode(String skuCode) {
            this.skuCode = skuCode;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public BigDecimal getImportPrice() {
            return importPrice;
        }

        public void setImportPrice(BigDecimal importPrice) {
            this.importPrice = importPrice;
        }

        public BigDecimal getSalePrice() {
            return salePrice;
        }

        public void setSalePrice(BigDecimal salePrice) {
            this.salePrice = salePrice;
        }

        public Integer getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }
    }

    // ===== Getters & Setters =====
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<VariantItem> getVariants() {
        return variants;
    }

    public void setVariants(List<VariantItem> variants) {
        this.variants = variants;
    }
}