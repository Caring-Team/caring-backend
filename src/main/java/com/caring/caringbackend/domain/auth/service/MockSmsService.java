package com.caring.caringbackend.domain.auth.service;

import org.springframework.stereotype.Service;

@Service
public class MockSmsService implements SmsService{
    @Override
    public boolean sendSms(String phoneNumber, String message) {
        return true;
    }
}
