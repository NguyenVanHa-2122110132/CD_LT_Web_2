package com.example.lt_web2.repository;

import com.example.lt_web2.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Integer> {

    // Lấy danh sách hội thoại, ưu tiên hiển thị Hot Lead lên đầu (FR-AI-004)
    List<ChatConversation> findAllByOrderByPriorityDescCreatedAtDesc();
}