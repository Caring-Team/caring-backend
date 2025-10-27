package com.caring.caringbackend.global.exception;

/**
 * 👤 회원을 찾을 수 없을 때 발생하는 예외
 * <p>
 * 회원 조회, 수정, 삭제 시 해당 ID의 회원이 존재하지 않을 때 사용합니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
public class MemberNotFoundException extends BusinessException {
    
    public MemberNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
    
    public MemberNotFoundException(Long memberId) {
        super(ErrorCode.USER_NOT_FOUND, "회원 ID: " + memberId);
    }
}
