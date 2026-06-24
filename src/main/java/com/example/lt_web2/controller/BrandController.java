package com.example.lt_web2.controller;

import com.example.lt_web2.entity.Brand;
import com.example.lt_web2.repository.BrandRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin(origins = "*")
public class BrandController {

    private final BrandRepository brandRepository;

    public BrandController(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @GetMapping
    public ResponseEntity<List<Brand>> getAll() {
        return ResponseEntity.ok(brandRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody Brand brand) {
        brandRepository.save(brand);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Thêm thương hiệu thành công!");
        return ResponseEntity.ok(res);
    }
}