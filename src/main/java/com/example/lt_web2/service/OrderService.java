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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private PromotionDetailRepository promotionDetailRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CustomerService customerService;

    private static final int CANCEL_TIME_LIMIT_MINUTES = 30; // BR-006

    // ====== FR-ORD-001: Tạo đơn hàng tại quầy ======
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest req) {

        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giỏ hàng không được để trống.");
        }
        if (req.getPaymentMethod() == null ||
                !(req.getPaymentMethod().equals("CASH") || req.getPaymentMethod().equals("QR"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Hình thức thanh toán không hợp lệ. Chỉ hỗ trợ CASH hoặc QR.");
        }

        // Khách hàng: nếu không có thì gán khách vãng lai (id = null trong order, không
        // bắt buộc FK)
        Customer customer = null;
        if (req.getCustomerId() != null) {
            customer = customerRepository.findById(req.getCustomerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));
        }

        Employee employee = null;
        if (req.getEmployeeId() != null) {
            employee = employeeRepository.findById(req.getEmployeeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên"));
        }

        // Tạo order trước (chưa có totalAmount) để có order.id gắn vào order_details
        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setCustomer(customer);
        order.setEmployee(employee);
        order.setStatus("PENDING");
        order = orderRepository.save(order);

        List<OrderDetail> savedDetails = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest item : req.getItems()) {

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Số lượng mua phải lớn hơn 0.");
            }

            // Khóa dòng SKU lại để trừ kho an toàn (tránh 2 đơn cùng lúc bán vượt tồn)
            ProductVariant variant = productVariantRepository.findByIdForUpdate(item.getVariantId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Không tìm thấy sản phẩm với ID: " + item.getVariantId()));

            if (item.getQuantity() > variant.getStockQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Sản phẩm \"" + variant.getSkuCode() + "\" chỉ còn " + variant.getStockQuantity()
                                + " trong kho, không đủ số lượng yêu cầu.");
            }

            // Tính giá thực bán: ưu tiên áp khuyến mãi đang active cho variant này (nếu có)
            BigDecimal actualPrice = calculateActualPrice(variant);

            // Trừ kho ngay (đã khóa dòng ở trên nên an toàn)
            variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
            productVariantRepository.save(variant);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProductVariant(variant);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(actualPrice);
            savedDetails.add(orderDetailRepository.save(detail));

            totalAmount = totalAmount.add(actualPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // Áp voucher nếu có (đơn giản: voucher giảm theo số tiền cố định/percent đã có
        // sẵn ở bảng vouchers)
        BigDecimal discountAmount = BigDecimal.ZERO;
        // Lưu ý: voucher tích điểm (FR-CUS-005) xử lý riêng ở
        // CustomerService.redeemPoints,
        // ở đây chỉ áp mã voucher nhập tay nếu bạn có bảng vouchers độc lập — để đơn
        // giản, bỏ qua nếu chưa cần.

        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setVoucherCode(req.getVoucherCode());
        order.setStatus("PENDING"); // chờ xác nhận thanh toán xong mới chuyển COMPLETED
        order = orderRepository.save(order);

        return toOrderResponse(order, savedDetails, req.getPaymentMethod());
    }

    // Tính giá bán thực tế sau khuyến mãi (nếu variant đang có khuyến mãi active)
    private BigDecimal calculateActualPrice(ProductVariant variant) {
        BigDecimal basePrice = variant.getSalePrice();

        return promotionDetailRepository.findActivePromotionForVariant(variant.getId(), LocalDateTime.now())
                .map(pd -> {
                    if ("PERCENT".equals(pd.getDiscountType())) {
                        BigDecimal discount = basePrice.multiply(pd.getDiscountValue())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        return basePrice.subtract(discount);
                    } else { // AMOUNT
                        return basePrice.subtract(pd.getDiscountValue());
                    }
                })
                .filter(price -> price.compareTo(BigDecimal.ZERO) > 0)
                .orElse(basePrice);
    }

    // Sinh mã hóa đơn: HD + YYYYMMDD + STT tăng dần trong ngày
    private synchronized String generateOrderCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long countToday = orderRepository.countOrdersToday();
        long nextSeq = countToday + 1;
        return String.format("HD%s%03d", datePart, nextSeq);
    }

    private OrderResponse toOrderResponse(Order order, List<OrderDetail> details, String paymentMethod) {
        List<OrderItemResponse> items = details.stream()
                .map(d -> new OrderItemResponse(
                        d.getProductVariant().getSkuCode(),
                        d.getProductVariant().getProduct().getName(),
                        d.getProductVariant().getColor(),
                        d.getProductVariant().getSize(),
                        d.getQuantity(),
                        d.getPrice()))
                .collect(Collectors.toList());

        BigDecimal finalAmount = order.getTotalAmount().subtract(order.getDiscountAmount());

        return new OrderResponse(
                order.getId(), order.getOrderCode(),
                order.getCustomer() != null ? order.getCustomer().getFullName() : "Khách vãng lai",
                order.getTotalAmount(), order.getDiscountAmount(), finalAmount,
                order.getStatus(), paymentMethod, order.getCreatedAt(), items);
    }

    // ====== FR-ORD-002: Thanh toán & hoàn tất đơn (áp dụng cho thanh toán CASH)
    // ======
    @Transactional
    public OrderResponse completeOrder(Integer orderId, String paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Đơn hàng không ở trạng thái chờ thanh toán.");
        }

        order.setStatus("COMPLETED");
        order.setPrintedAt(LocalDateTime.now()); // mốc thời gian in bill, dùng để tính hạn hủy đơn
        order = orderRepository.save(order);

        // Lưu thông tin thanh toán
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount().subtract(order.getDiscountAmount()));
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        // Tích điểm cho khách (nếu có)
        customerService.addPointsForCompletedOrder(order);

        // TODO: gọi driver máy in nhiệt K80 ở đây (FR-ORD-002) khi có thiết bị thật

        List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
        return toOrderResponse(order, details, paymentMethod);
    }

    // ====== FR-ORD-003: Hủy đơn hàng ======
    @Transactional
    public void cancelOrder(Integer orderId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        if ("CANCELLED".equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn hàng đã được hủy trước đó.");
        }

        // BR-006: kiểm tra thời gian từ lúc in hóa đơn
        if (order.getPrintedAt() != null) {
            long minutesPassed = java.time.Duration.between(order.getPrintedAt(), LocalDateTime.now()).toMinutes();
            if (minutesPassed > CANCEL_TIME_LIMIT_MINUTES && !isAdmin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Vượt quá 30 phút quy định, yêu cầu tài khoản Admin/Manager để thực hiện hủy đơn!");
            }
        }

        // Hoàn lại tồn kho cho từng sản phẩm trong đơn
        List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
        for (OrderDetail d : details) {
            ProductVariant variant = productVariantRepository.findByIdForUpdate(d.getProductVariant().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));
            variant.setStockQuantity(variant.getStockQuantity() + d.getQuantity());
            productVariantRepository.save(variant);
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
}