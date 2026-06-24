package com.example.lt_web2.repository;

import com.example.lt_web2.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // Lấy thông báo chưa đọc cho Admin/Manager
    List<Notification> findByIsReadFalseAndIsDeletedFalseOrderByCreatedAtDesc();
}