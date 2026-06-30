package com.example.lt_web2.service;

public interface NotificationService {
    void sendReminderToEmployee(String employeeName, String phoneOrChatId, String message);
}