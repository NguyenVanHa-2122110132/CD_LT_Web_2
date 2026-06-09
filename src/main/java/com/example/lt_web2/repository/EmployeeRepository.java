package com.example.lt_web2.repository;

import com.example.lt_web2.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // Tìm theo mã nhân viên
    Optional<Employee> findByEmployeeCode(String employeeCode);

    // Kiểm tra trùng lặp khi thêm mới
    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    // Kiểm tra trùng SĐT khi sửa (bỏ qua chính nhân viên đó)
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Integer id);

    // Lấy danh sách chưa bị xóa
    List<Employee> findByIsDeletedFalse();

    // Tìm kiếm theo tên hoặc mã nhân viên
    @Query("SELECT e FROM Employee e WHERE e.isDeleted = false AND " +
            "(LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Employee> searchByKeyword(@Param("keyword") String keyword);

    // Đếm đơn hàng để check trước khi xóa (dùng sau khi có bảng orders)
    // boolean existsOrdersByEmployeeId(Integer employeeId);

    // Lấy mã NV mới nhất theo format NV + YYMM
    @Query("SELECT e.employeeCode FROM Employee e WHERE e.employeeCode LIKE :prefix ORDER BY e.employeeCode DESC")
    List<String> findLatestCodeByPrefix(@Param("prefix") String prefix);
}