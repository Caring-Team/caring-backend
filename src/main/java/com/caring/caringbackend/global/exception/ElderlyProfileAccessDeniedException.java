package com.caring.caringbackend.global.exception;

/**
 * 🚫 어르신 프로필 접근 권한이 없을 때 발생하는 예외
 * <p>
 * 특정 어르신 프로필이 다른 회원의 소유이거나 접근할 권한이 없을 때 사용합니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
public class ElderlyProfileAccessDeniedException extends BusinessException {
    
    public ElderlyProfileAccessDeniedException() {
        super(ErrorCode.ELDERLY_PROFILE_ACCESS_DENIED);
    }
    
    public ElderlyProfileAccessDeniedException(Long memberId, Long profileId) {
        super(ErrorCode.ELDERLY_PROFILE_ACCESS_DENIED, 
              "회원 ID " + memberId + "는 프로필 ID " + profileId + "에 접근할 수 없습니다");
    }
}
