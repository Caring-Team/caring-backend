package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.api.auth.dto.request.SendCertificationCodeRequest;
import com.caring.caringbackend.api.auth.dto.request.VerifyPhoneRequest;
import com.caring.caringbackend.domain.auth.entity.CertificationCode;
import com.caring.caringbackend.domain.auth.repository.CertificationCodeRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service("inMemoryMockVerifyPhoneService")
public class MockVerifyPhoneService implements VerifyPhoneService {

    private final SmsService smsService;
    private final TransactionTemplate transactionTemplate;
    private final CertificationCodeRepository certificationCodeRepository;
    private final Long certificationCodeExpiresIn;

    @Override
    public void sendCertificationCode(SendCertificationCodeRequest certificationCodeRequest) {
        final String phoneNumber = certificationCodeRequest.getPhoneNumber();
        final String code = generateVerificationCode();
        if (!smsService.sendSms(phoneNumber, code)) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "인증번호 발송에 실패했습니다.");
        }

        transactionTemplate.executeWithoutResult(status -> {
            CertificationCode certificationCode = CertificationCode.builder()
                    .code(code)
                    .phone(phoneNumber)
                    .name(certificationCodeRequest.getName())
                    .birthDate(certificationCodeRequest.getBirthDate())
                    .expiresIn(certificationCodeExpiresIn).build();
            certificationCodeRepository.save(certificationCode);
        });
    }

    @Override
    public void verifyPhone(VerifyPhoneRequest verifyPhoneRequest) {
        CertificationCode certificationCode = certificationCodeRepository.findCertificationCodeByPhone(
                        verifyPhoneRequest.getPhoneNumber())
                .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "인증번호가 만료되었습니다."));

        if (!certificationCode.getCode().equals(verifyPhoneRequest.getCode())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "인증번호가 올바르지 않습니다.");
        }
        if (!certificationCode.getName().equals(verifyPhoneRequest.getName()) || !certificationCode.getBirthDate()
                .equals(verifyPhoneRequest.getBirthDate())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "올바르지 않은 인증 정보입니다.");
        }
        transactionTemplate.executeWithoutResult(status -> certificationCodeRepository.delete(certificationCode));
    }

    public String generateVerificationCode() {
        return "123456";
    }

    public MockVerifyPhoneService(SmsService smsService,
                                  CertificationCodeRepository certificationCodeRepository,
                                  TransactionTemplate transactionTemplate,
                                  @Value("${verify-phone.expires-in}") Long certificationCodeExpiresIn) {
        this.smsService = smsService;
        this.certificationCodeRepository = certificationCodeRepository;
        this.transactionTemplate = transactionTemplate;
        this.certificationCodeExpiresIn = certificationCodeExpiresIn;
    }
}
