package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.api.internal.auth.dto.request.SendCertificationCodeRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.VerifyPhoneRequest;

public interface VerifyPhoneService {

    public void sendCertificationCode(SendCertificationCodeRequest certificationCodeRequest);

    public void verifyPhone(VerifyPhoneRequest verifyPhoneRequest);
}
