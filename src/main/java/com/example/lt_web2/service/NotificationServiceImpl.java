package com.example.lt_web2.service;

import org.springframework.stereotype.Service;

// Stub tạm thời — thay bằng tích hợp thật với Telegram Bot API / Zalo OA khi có token thật
@Service
public class NotificationServiceImpl implements NotificationService {
    @Override
    public void sendReminderToEmployee(String employeeName, String phoneOrChatId, String message) {
        System.out.println("[NHẮC NHỞ CHẤM CÔNG -> " + employeeName + " (" + phoneOrChatId + ")]: " + message);
        // TODO: gọi API thật của Telegram Bot (sendMessage) hoặc Zalo OA tại đây
    }
}