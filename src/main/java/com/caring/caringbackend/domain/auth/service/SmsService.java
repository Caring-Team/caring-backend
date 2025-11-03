package com.caring.caringbackend.domain.auth.service;

public interface SmsService {
    public boolean sendSms(String phoneNumber, String message);
}
