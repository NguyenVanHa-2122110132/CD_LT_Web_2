package com.example.lt_web2.repository;

import com.example.lt_web2.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByCustomerCode(String customerCode);

    // SQL Server dùng TOP thay cho LIMIT
    @Query(value = "SELECT TOP 1 customer_code FROM customers ORDER BY id DESC", nativeQuery = true)
    String findLastCustomerCode();

    // FR-CUS-006: tìm khách có sinh nhật trùng ngày/tháng hiện tại
    @Query(value = "SELECT * FROM customers WHERE MONTH(birth_date) = :month " +
            "AND DAY(birth_date) = :day AND is_deleted = 0", nativeQuery = true)
    List<Customer> findCustomersWithBirthdayToday(int month, int day);
}