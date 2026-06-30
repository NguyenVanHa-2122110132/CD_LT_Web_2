package com.example.lt_web2.repository;

import com.example.lt_web2.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findByConversationIdOrderBySentAtAsc(Integer conversationId);
}