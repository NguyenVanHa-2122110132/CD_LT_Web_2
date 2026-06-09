package com.example.lt_web2.controller;

import com.example.lt_web2.entity.Employee;
import com.example.lt_web2.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // GET: Lấy tất cả nhân viên (có thể kèm tìm kiếm)
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(
            @RequestParam(required = false) String keyword) {
        List<Employee> employees = employeeService.searchEmployees(keyword);
        return ResponseEntity.ok(employees);
    }

    // GET: Lấy nhân viên theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET: Auto-gen mã nhân viên mới
    @GetMapping("/generate-code")
    public ResponseEntity<Map<String, String>> generateCode() {
        Map<String, String> response = new HashMap<>();
        response.put("employeeCode", employeeService.generateEmployeeCode());
        return ResponseEntity.ok(response);
    }

    // POST: Thêm nhân viên mới
    @PostMapping
    public ResponseEntity<Map<String, String>> createEmployee(
            @RequestBody Employee employee) {
        String message = employeeService.createEmployee(employee);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        if (message.equals("Thêm mới nhân viên thành công!")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // PUT: Cập nhật nhân viên
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateEmployee(
            @PathVariable Integer id,
            @RequestBody Employee employee) {
        String message = employeeService.updateEmployee(id, employee);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        if (message.equals("Cập nhật thông tin nhân viên thành công!")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // DELETE: Xóa nhân viên (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable Integer id) {
        String message = employeeService.deleteEmployee(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        if (message.equals("Xóa nhân viên thành công!")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // PATCH: Khóa tài khoản nhân viên
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateEmployee(@PathVariable Integer id) {
        String message = employeeService.deactivateEmployee(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        if (message.equals("Tài khoản nhân viên đã bị khóa!")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}