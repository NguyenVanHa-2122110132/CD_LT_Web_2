package com.example.lt_web2.controller;

import com.example.lt_web2.dto.ProductRequest;
import com.example.lt_web2.entity.Product;
import com.example.lt_web2.entity.ProductVariant;
import com.example.lt_web2.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")

public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET: Lấy tất cả sản phẩm
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET: Lấy biến thể của 1 sản phẩm
    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<ProductVariant>> getVariants(@PathVariable Integer productId) {
        return ResponseEntity.ok(productService.getVariantsByProduct(productId));
    }

    // FR-PRO-001: POST - Tạo sản phẩm cha + biến thể cùng lúc (@Transactional)
    @PostMapping
    public ResponseEntity<Map<String, String>> createProduct(@RequestBody ProductRequest request) {
        String message = productService.createProduct(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        if (message.equals("Thêm mới sản phẩm thành công!")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // FR-PRO-003: GET - Tìm kiếm sản phẩm theo keyword / danh mục / thương hiệu
    @GetMapping("/search")
    public ResponseEntity<List<ProductVariant>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer brandId) {
        return ResponseEntity.ok(productService.searchProducts(keyword, categoryId, brandId));
    }

    // FR-PRO-004: GET - Quét mã vạch / tìm theo SKU
    @GetMapping("/sku/{skuCode}")
    public ResponseEntity<Map<String, Object>> findBySku(@PathVariable String skuCode) {
        Map<String, Object> response = new HashMap<>();
        Optional<ProductVariant> variant = productService.findBySku(skuCode);

        if (variant.isPresent()) {
            response.put("found", true);
            response.put("variant", variant.get());
            return ResponseEntity.ok(response);
        }

        response.put("found", false);
        response.put("message", "Không tìm thấy sản phẩm có mã vạch này!");
        return ResponseEntity.ok(response);
    }
}