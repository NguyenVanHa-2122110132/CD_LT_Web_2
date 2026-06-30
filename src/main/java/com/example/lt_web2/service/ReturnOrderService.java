package com.example.lt_web2.service;

import com.example.lt_web2.dto.*;
import com.example.lt_web2.entity.*;
import com.example.lt_web2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ReturnOrderService {

    @Autowired
    private ReturnOrderRepository returnOrderRepository;
    @Autowired
    private ReturnOrderDetailRepository returnOrderDetailRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    private static final int RETURN_TIME_LIMIT_DAYS = 7; // BR-007
    private static final List<String> VALID_REASONS = List.of(
            "Hàng lỗi đường may", "Khách đổi size", "Khách đổi mẫu khác");

    // ====== FR-RET-001 + FR-RET-002 + FR-RET-003: Tạo phiếu đổi trả (toàn bộ xử lý
    // 1 luồng) ======
    @Transactional
    public ReturnOrderResponse createReturnOrder(ReturnOrderCreateRequest req) {

        // ----- FR-RET-001: Validation đầu vào -----
        if (req.getReason() == null || !VALID_REASONS.contains(req.getReason())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lý do đổi trả không hợp lệ. Chỉ chấp nhận: " + VALID_REASONS);
        }
        if (req.getReturnedItems() == null || req.getReturnedItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sách món trả không được để trống.");
        }

        // Tìm đơn hàng gốc
        Order originalOrder = orderRepository.findAll().stream()
                .filter(o -> req.getOriginalOrderCode().equalsIgnoreCase(o.getOrderCode()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy hóa đơn gốc với mã: " + req.getOriginalOrderCode()));

        // BR-007: kiểm tra thời hạn 7 ngày
        if (originalOrder.getCreatedAt() != null) {
            long daysPassed = java.time.Duration.between(originalOrder.getCreatedAt(), LocalDateTime.now()).toDays();
            if (daysPassed > RETURN_TIME_LIMIT_DAYS) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Quá thời hạn 7 ngày đổi trả quy định!");
            }
        }

        // Lấy danh sách món đã mua thực tế trên đơn gốc
        List<OrderDetail> originalDetails = orderDetailRepository.findByOrderId(originalOrder.getId());

        // Validate: số lượng trả phải <= số lượng đã mua thực tế
        for (ReturnItemRequest item : req.getReturnedItems()) {
            int purchasedQty = originalDetails.stream()
                    .filter(d -> d.getProductVariant().getId().equals(item.getVariantId()))
                    .mapToInt(OrderDetail::getQuantity)
                    .sum();
            if (item.getQuantity() == null || item.getQuantity() <= 0 || item.getQuantity() > purchasedQty) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Số lượng trả của SKU ID " + item.getVariantId() +
                                " vượt quá số lượng đã mua thực tế (" + purchasedQty + ").");
            }
        }

        // Tạo phiếu đổi trả
        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setReturnCode(generateReturnCode());
        returnOrder.setOrder(originalOrder);
        returnOrder.setReason(req.getReason());
        returnOrder.setStatus("PENDING");
        returnOrder = returnOrderRepository.save(returnOrder);

        // Lưu chi tiết món trả + tính tổng giá trị đồ cũ
        BigDecimal oldItemsTotal = BigDecimal.ZERO;
        for (ReturnItemRequest item : req.getReturnedItems()) {
            ProductVariant variant = productVariantRepository.findById(item.getVariantId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Không tìm thấy sản phẩm với ID: " + item.getVariantId()));

            ReturnOrderDetail detail = new ReturnOrderDetail();
            detail.setReturnOrder(returnOrder);
            detail.setProductVariant(variant);
            detail.setQuantity(item.getQuantity());
            returnOrderDetailRepository.save(detail);

            // ----- FR-RET-003: hoàn kho đồ trả -----
            ProductVariant lockedVariant = productVariantRepository.findByIdForUpdate(variant.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

            if ("Hàng lỗi đường may".equals(req.getReason())) {
                // Hàng lỗi: KHÔNG cộng lại vào kho bán bình thường (coi như hủy, không cộng
                // stockQuantity)
                // Ghi chú lại trong note để theo dõi kho hủy (DEFECTIVE) — chưa có bảng kho hủy
                // riêng
                detail.setNote("DEFECTIVE - không nhập lại kho bán");
            } else {
                // Đổi size/mẫu: cộng ngược lại vào kho bán bình thường để tái sử dụng
                lockedVariant.setStockQuantity(lockedVariant.getStockQuantity() + item.getQuantity());
                productVariantRepository.save(lockedVariant);
                detail.setNote("RESALABLE - đã nhập lại kho bán");
            }
            returnOrderDetailRepository.save(detail);

            oldItemsTotal = oldItemsTotal.add(variant.getSalePrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // Lấy danh sách món mới (nếu có) + trừ kho ngay
        BigDecimal newItemsTotal = BigDecimal.ZERO;
        if (req.getNewItems() != null) {
            for (ReturnItemRequest item : req.getNewItems()) {
                ProductVariant newVariant = productVariantRepository.findByIdForUpdate(item.getVariantId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Không tìm thấy sản phẩm mới với ID: " + item.getVariantId()));

                if (item.getQuantity() > newVariant.getStockQuantity()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Sản phẩm \"" + newVariant.getSkuCode() + "\" không đủ tồn kho cho món lấy mới.");
                }

                // FR-RET-003: trừ kho cho món lấy mới
                newVariant.setStockQuantity(newVariant.getStockQuantity() - item.getQuantity());
                productVariantRepository.save(newVariant);

                newItemsTotal = newItemsTotal.add(
                        newVariant.getSalePrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        // ----- FR-RET-002: Xử lý lệch giá -----
        BigDecimal differenceAmount = newItemsTotal.subtract(oldItemsTotal);
        String refundVoucherCode = null;
        String qrPaymentNote = null;

        if (differenceAmount.compareTo(BigDecimal.ZERO) > 0) {
            // Đồ mới đắt hơn -> thu thêm tiền chênh lệch qua QR động (stub, chưa nối cổng
            // thanh toán thật)
            qrPaymentNote = "Cần thu thêm " + differenceAmount + " đ qua QR động cho phiếu "
                    + returnOrder.getReturnCode();
        } else if (differenceAmount.compareTo(BigDecimal.ZERO) < 0) {
            // Đồ cũ đắt hơn -> sinh voucher hoàn tiền (BR-012)
            BigDecimal refundValue = differenceAmount.abs();
            Voucher refundVoucher = new Voucher();
            refundVoucher.setVoucherCode("REFUND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            refundVoucher.setDiscountType("AMOUNT");
            refundVoucher.setDiscountValue(refundValue);
            refundVoucher.setMinOrderValue(BigDecimal.ZERO);
            refundVoucher.setMaxDiscount(refundValue);
            refundVoucher.setUsageLimit(1);
            refundVoucher.setUsedCount(0);
            refundVoucher.setStartDate(LocalDateTime.now());
            refundVoucher.setEndDate(LocalDateTime.now().plusMonths(3));
            refundVoucher.setStatus("ACTIVE");
            refundVoucher.setDescription("Voucher hoàn tiền từ phiếu đổi trả " + returnOrder.getReturnCode());
            refundVoucher = voucherRepository.save(refundVoucher);

            refundVoucherCode = refundVoucher.getVoucherCode();
        }

        returnOrder.setDifferenceAmount(differenceAmount);
        returnOrder.setRefundVoucherCode(refundVoucherCode);
        returnOrder.setStatus("COMPLETED");
        returnOrder = returnOrderRepository.save(returnOrder);

        return new ReturnOrderResponse(returnOrder.getId(), returnOrder.getReturnCode(), returnOrder.getReason(),
                returnOrder.getStatus(), oldItemsTotal, newItemsTotal, differenceAmount, refundVoucherCode,
                qrPaymentNote);
    }

    private synchronized String generateReturnCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long countToday = returnOrderRepository.countReturnsToday();
        return String.format("DT%s%03d", datePart, countToday + 1);
    }
}