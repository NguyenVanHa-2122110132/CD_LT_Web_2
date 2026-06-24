package com.example.lt_web2.service;

import com.example.lt_web2.dto.ProductRequest;
import com.example.lt_web2.entity.*;
import com.example.lt_web2.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final NotificationRepository notificationRepository;

    public ProductService(ProductRepository productRepository,
            ProductVariantRepository variantRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            NotificationRepository notificationRepository) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.notificationRepository = notificationRepository;
    }

    // ===================== FR-PRO-001: TẠO SẢN PHẨM CHA + BIẾN THỂ
    // =====================
    @Transactional
    public String createProduct(ProductRequest request) {

        // Validate mã sản phẩm cha
        if (request.getProductCode() == null || request.getProductCode().isBlank()) {
            return "Mã sản phẩm không được để trống";
        }
        if (productRepository.existsByProductCode(request.getProductCode())) {
            return "Mã sản phẩm '" + request.getProductCode() + "' đã tồn tại trong hệ thống";
        }

        // Validate tên sản phẩm
        if (request.getName() == null || request.getName().isBlank()) {
            return "Tên sản phẩm không được để trống";
        }
        if (request.getName().length() > 150) {
            return "Tên sản phẩm không được vượt quá 150 ký tự";
        }

        // Validate danh mục
        if (request.getCategoryId() == null) {
            return "Danh mục không được để trống";
        }
        Optional<Category> category = categoryRepository.findById(request.getCategoryId());
        if (category.isEmpty()) {
            return "Danh mục không tồn tại";
        }

        // Validate thương hiệu
        if (request.getBrandId() == null) {
            return "Thương hiệu không được để trống";
        }
        Optional<Brand> brand = brandRepository.findById(request.getBrandId());
        if (brand.isEmpty()) {
            return "Thương hiệu không tồn tại";
        }

        // Validate danh sách biến thể
        if (request.getVariants() == null || request.getVariants().isEmpty()) {
            return "Sản phẩm phải có ít nhất 1 biến thể (SKU)";
        }

        // Validate từng biến thể trước khi lưu — rollback nếu có lỗi
        List<String> skuList = new ArrayList<>();
        for (int i = 0; i < request.getVariants().size(); i++) {
            ProductRequest.VariantItem item = request.getVariants().get(i);

            // Kiểm tra trùng SKU trong request
            if (skuList.contains(item.getSkuCode())) {
                return "Dòng " + (i + 1) + ": Mã SKU '" + item.getSkuCode() + "' bị trùng lặp trong danh sách";
            }
            skuList.add(item.getSkuCode());

            // Kiểm tra trùng SKU trong DB
            if (variantRepository.existsBySkuCode(item.getSkuCode())) {
                return "Dòng " + (i + 1) + ": Mã SKU '" + item.getSkuCode() + "' đã tồn tại trong hệ thống";
            }

            // Kiểm tra giá bán >= giá nhập
            if (item.getImportPrice() == null || item.getSalePrice() == null) {
                return "Dòng " + (i + 1) + ": Giá nhập và giá bán không được để trống";
            }
            if (item.getSalePrice().compareTo(item.getImportPrice()) < 0) {
                return "Dòng " + (i + 1) + ": Giá bán không được nhỏ hơn giá nhập (SKU: " + item.getSkuCode() + ")";
            }

            // Kiểm tra tồn kho >= 0
            if (item.getStockQuantity() == null || item.getStockQuantity() < 0) {
                return "Dòng " + (i + 1) + ": Số lượng tồn kho không hợp lệ";
            }
        }

        // Lưu sản phẩm cha
        Product product = new Product();
        product.setProductCode(request.getProductCode());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category.get());
        product.setBrand(brand.get());
        product.setImage(request.getImage());
        product.setPrice(request.getVariants().get(0).getSalePrice()); // Giá đại diện
        product.setIsDeleted(false);
        productRepository.save(product);

        // Lưu danh sách biến thể trong cùng @Transactional
        for (ProductRequest.VariantItem item : request.getVariants()) {
            ProductVariant variant = new ProductVariant();
            variant.setSkuCode(item.getSkuCode());
            variant.setColor(item.getColor());
            variant.setSize(item.getSize());
            variant.setImportPrice(item.getImportPrice());
            variant.setSalePrice(item.getSalePrice());
            variant.setStockQuantity(item.getStockQuantity());
            variant.setBarcode(item.getBarcode());
            variant.setProduct(product);
            variantRepository.save(variant);
        }

        return "Thêm mới sản phẩm thành công!";
    }

    // ===================== FR-PRO-003: TÌM KIẾM SẢN PHẨM =====================
    public List<ProductVariant> searchProducts(String keyword, Integer categoryId, Integer brandId) {
        return variantRepository.searchVariants(keyword, categoryId, brandId);
    }

    // ===================== FR-PRO-004: QUÉT MÃ VẠCH =====================
    public Optional<ProductVariant> findBySku(String skuCode) {
        return variantRepository.findBySkuCode(skuCode);
    }

    // ===================== FR-PRO-005: CẢNH BÁO TỒN KHO =====================
    // Gọi hàm này sau mỗi lần trừ tồn kho (khi có đơn hàng hoặc xuất kho)
    @Transactional
    public void checkAndNotifyLowStock(ProductVariant variant) {
        if (variant.getStockQuantity() < 10) {
            Notification notification = new Notification();
            notification.setTitle("Cảnh báo tồn kho thấp");
            notification.setContent("Sản phẩm [" + variant.getSkuCode() + "] - "
                    + variant.getProduct().getName()
                    + " (Màu: " + variant.getColor() + ", Size: " + variant.getSize() + ")"
                    + " chỉ còn " + variant.getStockQuantity() + " sản phẩm trong kho!");
            notification.setType("LOW_STOCK");
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    // Lấy toàn bộ sản phẩm
    public List<Product> getAllProducts() {
        return productRepository.findByIsDeletedFalse();
    }

    // Lấy chi tiết biến thể của 1 sản phẩm
    public List<ProductVariant> getVariantsByProduct(Integer productId) {
        return variantRepository.findByProductId(productId);
    }
}