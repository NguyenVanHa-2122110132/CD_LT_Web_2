package com.example.lt_web2.service;

import com.example.lt_web2.dto.ComboPolicyRequest;
import com.example.lt_web2.dto.ComboPolicyResponse;
import com.example.lt_web2.entity.Category;
import com.example.lt_web2.entity.ComboPolicy;
import com.example.lt_web2.repository.CategoryRepository;
import com.example.lt_web2.repository.ComboPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class ComboPolicyService {

    @Autowired
    private ComboPolicyRepository comboPolicyRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    // ====== FR-PRM-003: Thiết lập Combo ======
    public ComboPolicyResponse createCombo(ComboPolicyRequest req) {

        if (req.getComboName() == null || req.getComboName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên combo không được để trống.");
        }
        if (req.getRequiredQuantity() == null || req.getRequiredQuantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng yêu cầu phải > 0.");
        }
        if (req.getFixedPrice() == null || req.getFixedPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá combo cố định phải > 0.");
        }

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục sản phẩm"));

        ComboPolicy combo = new ComboPolicy();
        combo.setComboName(req.getComboName());
        combo.setCategory(category);
        combo.setRequiredQuantity(req.getRequiredQuantity());
        combo.setFixedPrice(req.getFixedPrice());
        combo.setIsActive(true);

        combo = comboPolicyRepository.save(combo);

        return new ComboPolicyResponse(combo.getId(), combo.getComboName(), category.getName(),
                combo.getRequiredQuantity(), combo.getFixedPrice());
    }
}