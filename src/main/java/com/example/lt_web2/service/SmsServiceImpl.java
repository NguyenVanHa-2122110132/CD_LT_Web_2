package com.example.lt_web2.service;

import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public void sendSms(String phoneNumber, String message) {
        System.out.println("[SMS to " + phoneNumber + "]: " + message);
        // TODO: thay bằng tích hợp thật với SMS Brandname/Zalo OA
    }
}