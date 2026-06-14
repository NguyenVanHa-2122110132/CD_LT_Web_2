package com.example.lt_web2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private ChatConversation conversation;

    private String senderType;

    @Column(columnDefinition = "nvarchar(max)")
    private String message;

    private LocalDateTime sentAt;

    // Getter Setter
}