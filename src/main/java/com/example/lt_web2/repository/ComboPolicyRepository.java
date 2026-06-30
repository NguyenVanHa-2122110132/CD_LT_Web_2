package com.example.lt_web2.repository;

import com.example.lt_web2.entity.ComboPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ComboPolicyRepository extends JpaRepository<ComboPolicy, Integer> {

    List<ComboPolicy> findByIsActiveTrueAndIsDeletedFalse();

    Optional<ComboPolicy> findByCategoryIdAndIsActiveTrueAndIsDeletedFalse(Integer categoryId);
}