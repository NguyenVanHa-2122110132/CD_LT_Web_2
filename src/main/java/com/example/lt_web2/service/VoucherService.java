package com.example.lt_web2.service;

import com.example.lt_web2.dto.VoucherCreateRequest;
import com.example.lt_web2.dto.VoucherResponse;
import com.example.lt_web2.entity.Voucher;
import com.example.lt_web2.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    // ====== FR-PRM-002: Phát hành mã Voucher ======
    public VoucherResponse createVoucher(VoucherCreateRequest req) {

        if (req.getVoucherCode() == null || req.getVoucherCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã Voucher không được để trống.");
        }
        if (voucherRepository.existsByVoucherCode(req.getVoucherCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã Voucher đã tồn tại trên hệ thống!");
        }
        if (req.getDiscountType() == null ||
                !(req.getDiscountType().equals("PERCENT") || req.getDiscountType().equals("AMOUNT"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại giảm giá không hợp lệ.");
        }
        if (req.getMinOrderValue() == null || req.getMinOrderValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn tối thiểu phải là số >= 0.");
        }
        if (req.getMaxDiscount() == null || req.getMaxDiscount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giảm tối đa phải là số > 0.");
        }
        if (req.getUsageLimit() == null || req.getUsageLimit() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tổng lượt dùng phải là số nguyên > 0.");
        }
        if (req.getStartDate() == null || req.getEndDate() == null || !req.getEndDate().isAfter(req.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày kết thúc phải lớn hơn ngày bắt đầu.");
        }

        // Validate giá trị giảm theo loại
        if (req.getDiscountType().equals("PERCENT")) {
            if (req.getDiscountValue() == null || req.getDiscountValue().compareTo(BigDecimal.ONE) < 0
                    || req.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá trị giảm theo % phải từ 1 đến 100.");
            }
        } else { // AMOUNT
            if (req.getDiscountValue() == null || req.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0
                    || req.getDiscountValue().compareTo(req.getMinOrderValue()) >= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Giá trị giảm theo số tiền phải > 0 và nhỏ hơn đơn tối thiểu.");
            }
        }

        Voucher voucher = new Voucher();
        voucher.setVoucherCode(req.getVoucherCode().toUpperCase());
        voucher.setDiscountType(req.getDiscountType());
        voucher.setDiscountValue(req.getDiscountValue());
        voucher.setMinOrderValue(req.getMinOrderValue());
        voucher.setMaxDiscount(req.getMaxDiscount());
        voucher.setUsageLimit(req.getUsageLimit());
        voucher.setUsedCount(0);
        voucher.setStartDate(req.getStartDate());
        voucher.setEndDate(req.getEndDate());
        voucher.setStatus("ACTIVE");
        voucher.setDescription(req.getDescription());

        voucher = voucherRepository.save(voucher);

        return new VoucherResponse(voucher.getId(), voucher.getVoucherCode(), voucher.getDiscountType(),
                voucher.getDiscountValue(), voucher.getMinOrderValue(), voucher.getMaxDiscount(),
                voucher.getUsageLimit(), voucher.getStartDate(), voucher.getEndDate(), voucher.getStatus());
    }
}