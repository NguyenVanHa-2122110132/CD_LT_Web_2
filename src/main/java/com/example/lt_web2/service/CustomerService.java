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
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PointHistoryRepository pointHistoryRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^(03|05|07|08|09)\\d{8}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[^!@#$%^&*()_+=\\[\\]{};:\"\\\\|,.<>/?`~]{1,50}$");

    // FR-CUS-001: Thêm nhanh khách hàng
    @Transactional
    public CustomerResponse quickAddCustomer(CustomerCreateRequest req) {

        if (req.getPhoneNumber() == null || !PHONE_PATTERN.matcher(req.getPhoneNumber()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Số điện thoại không hợp lệ. Phải gồm 10 số và bắt đầu bằng 03/05/07/08/09.");
        }

        if (customerRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Số điện thoại này đã được đăng ký cho khách hàng khác!");
        }

        if (req.getFullName() == null || !NAME_PATTERN.matcher(req.getFullName()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Họ tên không hợp lệ. Tối đa 50 ký tự, không chứa ký tự đặc biệt.");
        }

        if (req.getEmail() != null && !req.getEmail().isBlank()
                && !EMAIL_PATTERN.matcher(req.getEmail()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không đúng định dạng.");
        }

        Customer customer = new Customer();
        customer.setCustomerCode(generateNextCustomerCode());
        customer.setFullName(req.getFullName());
        customer.setPhoneNumber(req.getPhoneNumber());
        customer.setEmail(req.getEmail());
        customer.setAddress(req.getAddress());
        customer.setTotalPoints(0);

        customer = customerRepository.save(customer);
        return toResponse(customer);
    }

    private synchronized String generateNextCustomerCode() {
        String last = customerRepository.findLastCustomerCode();
        int next = 1;
        if (last != null && last.startsWith("KH")) {
            try {
                next = Integer.parseInt(last.substring(2)) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("KH%05d", next);
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(c.getId(), c.getCustomerCode(), c.getFullName(),
                c.getPhoneNumber(), c.getEmail(), c.getAddress(), c.getTotalPoints());
    }

    // FR-CUS-003: Lịch sử mua hàng
    public List<OrderHistoryResponse> getPurchaseHistory(Integer customerId) {
        List<Order> orders = orderRepository
                .findByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(customerId);
        return orders.stream()
                .map(o -> new OrderHistoryResponse(o.getOrderCode(), o.getCreatedAt(),
                        o.getTotalAmount(), o.getStatus()))
                .collect(Collectors.toList());
    }

    // FR-CUS-004: Quét mã QR thành viên
    public CustomerResponse findByQrCode(String code) {
        Customer customer = customerRepository.findByCustomerCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khách hàng với mã: " + code));
        return toResponse(customer);
    }

    // FR-CUS-002: Tích điểm tự động khi đơn hàng thành công
    @Transactional
    public void addPointsForCompletedOrder(Order order) {
        if (order.getCustomer() == null)
            return;

        List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());

        BigDecimal fullPriceTotal = details.stream()
                .filter(d -> d.getProductVariant() != null
                        && d.getPrice().compareTo(d.getProductVariant().getSalePrice()) == 0)
                .map(d -> d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int pointsToAdd = fullPriceTotal
                .divide(BigDecimal.valueOf(10000), 0, java.math.RoundingMode.DOWN)
                .intValue();

        if (pointsToAdd <= 0)
            return;

        Customer customer = order.getCustomer();
        customer.setTotalPoints(customer.getTotalPoints() + pointsToAdd);
        customerRepository.save(customer);

        PointHistory history = new PointHistory();
        history.setCustomer(customer);
        history.setPoints(pointsToAdd);
        history.setType("EARN");
        history.setDescription("Tích điểm từ đơn hàng " + order.getOrderCode());
        pointHistoryRepository.save(history);
    }

    // Lấy danh sách toàn bộ khách hàng (dùng cho trang quản lý frontend)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .filter(c -> !c.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // FR-CUS-005: Đổi điểm lấy giảm giá
    @Transactional
    public RedeemPointResponse redeemPoints(RedeemPointRequest req) {
        Customer customer = customerRepository.findById(req.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khách hàng"));

        BigDecimal discount;
        if (req.getPointsToRedeem() == 100) {
            discount = BigDecimal.valueOf(50000);
        } else if (req.getPointsToRedeem() == 500) {
            discount = BigDecimal.valueOf(300000);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mốc điểm đổi không hợp lệ. Chỉ hỗ trợ 100 hoặc 500 điểm.");
        }

        if (customer.getTotalPoints() < req.getPointsToRedeem()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Khách hàng không đủ điểm để đổi.");
        }

        customer.setTotalPoints(customer.getTotalPoints() - req.getPointsToRedeem());
        customerRepository.save(customer);

        PointHistory history = new PointHistory();
        history.setCustomer(customer);
        history.setPoints(-req.getPointsToRedeem());
        history.setType("REDEEM");
        history.setDescription("Đổi " + req.getPointsToRedeem() + " điểm lấy giảm giá " + discount);
        pointHistoryRepository.save(history);

        return new RedeemPointResponse(discount, customer.getTotalPoints());
    }
}