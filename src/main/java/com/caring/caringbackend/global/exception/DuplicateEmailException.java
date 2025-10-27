package com.caring.caringbackend.global.exception;

/**
 * 📧 중복된 이메일로 회원 가입을 시도할 때 발생하는 예외
 * <p>
 * 회원 가입 시 이미 존재하는 이메일로 등록을 시도할 때 사용합니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
public class DuplicateEmailException extends BusinessException {
    
    public DuplicateEmailException() {
        super(ErrorCode.EMAIL_ALREADY_EXISTS);
    }
    
    public DuplicateEmailException(String email) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, "이미 사용 중인 이메일: " + email);
    }
}
