package com.example.lt_web2.service;

import com.example.lt_web2.entity.Employee;
import com.example.lt_web2.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder; // [FIX #1] Inject BCrypt

    public EmployeeService(EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===================== AUTO-GEN MÃ NHÂN VIÊN =====================
    // Format: NV + YYMM + STT 3 chữ số (VD: NV2606001)
    public String generateEmployeeCode() {
        String yymm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM"));
        String prefix = "NV" + yymm;
        List<String> codes = employeeRepository.findLatestCodeByPrefix(prefix + "%");
        if (codes.isEmpty()) {
            return prefix + "001";
        }
        String latest = codes.get(0);
        int seq = Integer.parseInt(latest.substring(6)) + 1;
        return prefix + String.format("%03d", seq);
    }

    // ===================== LẤY DANH SÁCH =====================
    public List<Employee> getAllEmployees() {
        return employeeRepository.findByIsDeletedFalse();
    }

    public Optional<Employee> getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .filter(e -> !e.getIsDeleted());
    }

    public List<Employee> searchEmployees(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllEmployees();
        }
        return employeeRepository.searchByKeyword(keyword.trim());
    }

    // ===================== THÊM MỚI — FR-EMP-001 =====================
    public String createEmployee(Employee employee) {

        // Kiểm tra tuổi >= 18
        if (employee.getDateOfBirth() == null) {
            return "Ngày sinh không được để trống";
        }
        int age = Period.between(employee.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < 18) {
            return "Nhân viên chưa đủ 18 tuổi";
        }

        // Kiểm tra giới tính
        List<String> validGenders = List.of("Nam", "Nữ", "Khác");
        if (employee.getGender() == null || !validGenders.contains(employee.getGender())) {
            return "Giới tính không hợp lệ (Nam / Nữ / Khác)";
        }

        // Kiểm tra chức vụ
        List<String> validPositions = List.of("Admin", "Quản lý cửa hàng", "Nhân viên bán hàng");
        if (employee.getPosition() == null || !validPositions.contains(employee.getPosition())) {
            return "Chức vụ không hợp lệ (Admin / Quản lý cửa hàng / Nhân viên bán hàng)";
        }

        // [FIX #1b] Ngày vào làm: bắt buộc có, không được là ngày quá khứ
        if (employee.getStartDate() == null) {
            return "Ngày vào làm không được để trống";
        }
        if (employee.getStartDate().isBefore(LocalDate.now())) {
            return "Ngày vào làm không được là ngày trong quá khứ";
        }

        // Kiểm tra trùng SĐT
        if (employeeRepository.existsByPhoneNumber(employee.getPhoneNumber())) {
            return "Số điện thoại đã được sử dụng";
        }

        // Kiểm tra trùng email
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            return "Email đã tồn tại trên hệ thống";
        }

        // Auto-gen mã nhân viên
        employee.setEmployeeCode(generateEmployeeCode());
        employee.setIsActive(true);
        employee.setIsDeleted(false);

        // [FIX #1] Mã hóa mật khẩu bằng BCrypt trước khi lưu
        if (employee.getPassword() == null || employee.getPassword().isBlank()) {
            return "Mật khẩu không được để trống";
        }
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        employeeRepository.save(employee);
        return "Thêm mới nhân viên thành công!";
    }

    // ===================== CẬP NHẬT — FR-EMP-002 =====================
    public String updateEmployee(Integer id, Employee employee) {
        Optional<Employee> existing = employeeRepository.findById(id);
        if (existing.isEmpty() || existing.get().getIsDeleted()) {
            return "Nhân viên không tồn tại";
        }

        Employee emp = existing.get();

        // Kiểm tra tuổi >= 18
        if (employee.getDateOfBirth() == null) {
            return "Ngày sinh không được để trống";
        }
        int age = Period.between(employee.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < 18) {
            return "Nhân viên chưa đủ 18 tuổi";
        }

        // Kiểm tra giới tính
        List<String> validGenders = List.of("Nam", "Nữ", "Khác");
        if (employee.getGender() == null || !validGenders.contains(employee.getGender())) {
            return "Giới tính không hợp lệ (Nam / Nữ / Khác)";
        }

        // Kiểm tra chức vụ
        List<String> validPositions = List.of("Admin", "Quản lý cửa hàng", "Nhân viên bán hàng");
        if (employee.getPosition() == null || !validPositions.contains(employee.getPosition())) {
            return "Chức vụ không hợp lệ (Admin / Quản lý cửa hàng / Nhân viên bán hàng)";
        }

        // Kiểm tra trùng SĐT với nhân viên khác
        if (employeeRepository.existsByPhoneNumberAndIdNot(employee.getPhoneNumber(), id)) {
            return "Số điện thoại đã được sử dụng bởi nhân viên khác";
        }

        // [FR-EMP-002] employeeCode và email KHÔNG được sửa (read-only)
        emp.setFullName(employee.getFullName());
        emp.setDateOfBirth(employee.getDateOfBirth());
        emp.setGender(employee.getGender());
        emp.setPhoneNumber(employee.getPhoneNumber());
        emp.setPosition(employee.getPosition());
        emp.setStartDate(employee.getStartDate());

        employeeRepository.save(emp);
        return "Cập nhật thông tin nhân viên thành công!";
    }

    // ===================== XÓA — FR-EMP-003 =====================
    public String deleteEmployee(Integer id) {
        Optional<Employee> existing = employeeRepository.findById(id);
        if (existing.isEmpty() || existing.get().getIsDeleted()) {
            return "Nhân viên không tồn tại";
        }

        Employee emp = existing.get();

        // [FIX #2] Kiểm tra lịch sử đơn hàng hoặc chấm công
        boolean hasOrders = employeeRepository.existsOrdersByEmployeeId(id);
        boolean hasAttendance = employeeRepository.existsAttendanceByEmployeeId(id);

        if (hasOrders || hasAttendance) {
            // Trường hợp 1: Đã có lịch sử → chỉ vô hiệu hóa, không xóa vật lý
            emp.setIsActive(false);
            employeeRepository.save(emp);
            return "Không thể xóa nhân viên đã có lịch sử bán hàng. " +
                    "Hệ thống tự động chuyển trạng thái tài khoản sang 'Ngừng hoạt động'!";
        }

        // Trường hợp 2: Chưa có lịch sử → Soft delete
        emp.setIsDeleted(true);
        emp.setIsActive(false);
        employeeRepository.save(emp);
        return "Xóa nhân viên thành công!";
    }

    // ===================== KHÓA TÀI KHOẢN =====================
    public String deactivateEmployee(Integer id) {
        Optional<Employee> existing = employeeRepository.findById(id);
        if (existing.isEmpty()) {
            return "Nhân viên không tồn tại";
        }
        Employee emp = existing.get();
        emp.setIsActive(false);
        employeeRepository.save(emp);
        return "Tài khoản nhân viên đã bị khóa!";
    }
}