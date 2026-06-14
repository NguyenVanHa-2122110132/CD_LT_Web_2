package com.example.lt_web2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_conversations")
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String status;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "conversation")
    private List<ChatMessage> messages;

    // Getter Setter
}