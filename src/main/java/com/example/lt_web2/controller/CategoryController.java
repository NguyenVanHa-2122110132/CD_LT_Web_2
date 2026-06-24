package com.example.lt_web2.controller;

import com.example.lt_web2.entity.Category;
import com.example.lt_web2.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryRepository.findByIsDeletedFalse());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody Category category) {
        category.setIsDeleted(false);
        categoryRepository.save(category);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Thêm danh mục thành công!");
        return ResponseEntity.ok(res);
    }
}