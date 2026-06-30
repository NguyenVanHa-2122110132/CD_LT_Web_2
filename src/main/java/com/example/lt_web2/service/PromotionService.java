package com.example.lt_web2.service;

import com.example.lt_web2.dto.*;
import com.example.lt_web2.entity.*;
import com.example.lt_web2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PromotionDetailRepository promotionDetailRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;

    // ====== FR-PRM-001: Tạo chiến dịch Flash Sale ======
    @Transactional
    public PromotionResponse createPromotion(PromotionCreateRequest req) {

        if (req.getPromotionName() == null || req.getPromotionName().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tên chiến dịch không hợp lệ (tối đa 100 ký tự).");
        }
        if (req.getStartDate() == null || req.getStartDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Thời gian bắt đầu không được là thời gian quá khứ.");
        }
        if (req.getEndDate() == null || !req.getEndDate().isAfter(req.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Thời gian kết thúc phải lớn hơn thời gian bắt đầu.");
        }
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sách áp dụng không được để trống.");
        }

        // Kiểm tra từng SKU có bị chồng chéo với chiến dịch khác đang ACTIVE không
        for (PromotionItemRequest item : req.getItems()) {
            boolean overlap = promotionDetailRepository.existsOverlappingPromotion(
                    item.getVariantId(), req.getStartDate(), req.getEndDate());
            if (overlap) {
                ProductVariant v = productVariantRepository.findById(item.getVariantId()).orElse(null);
                String skuLabel = v != null ? v.getSkuCode() : ("ID " + item.getVariantId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "SKU \"" + skuLabel + "\" đang tham gia một chương trình khuyến mãi khác trùng thời gian.");
            }
        }

        Promotion promotion = new Promotion();
        promotion.setPromotionName(req.getPromotionName());
        promotion.setStartDate(req.getStartDate());
        promotion.setEndDate(req.getEndDate());
        promotion.setStatus("ACTIVE");
        promotion = promotionRepository.save(promotion);

        for (PromotionItemRequest item : req.getItems()) {
            ProductVariant variant = productVariantRepository.findById(item.getVariantId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Không tìm thấy sản phẩm với ID: " + item.getVariantId()));

            PromotionDetail detail = new PromotionDetail();
            detail.setPromotion(promotion);
            detail.setProductVariant(variant);
            detail.setDiscountType(item.getDiscountType());
            detail.setDiscountValue(item.getDiscountValue());
            promotionDetailRepository.save(detail);
        }

        return new PromotionResponse(promotion.getId(), promotion.getPromotionName(),
                promotion.getStartDate(), promotion.getEndDate(), promotion.getStatus(), req.getItems().size());
    }
}