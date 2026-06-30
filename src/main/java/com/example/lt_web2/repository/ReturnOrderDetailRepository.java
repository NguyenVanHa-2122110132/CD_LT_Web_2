package com.example.lt_web2.repository;

import com.example.lt_web2.entity.ReturnOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReturnOrderDetailRepository extends JpaRepository<ReturnOrderDetail, Integer> {
    List<ReturnOrderDetail> findByReturnOrderId(Integer returnOrderId);
}