package com.caring.caringbackend.global.exception;

/**
 * 👵 어르신 프로필을 찾을 수 없을 때 발생하는 예외
 * <p>
 * 어르신 프로필 조회, 수정, 삭제 시 해당 ID의 프로필이 존재하지 않을 때 사용합니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
public class ElderlyProfileNotFoundException extends BusinessException {
    
    public ElderlyProfileNotFoundException() {
        super(ErrorCode.ELDERLY_PROFILE_NOT_FOUND);
    }
    
    public ElderlyProfileNotFoundException(Long profileId) {
        super(ErrorCode.ELDERLY_PROFILE_NOT_FOUND, "어르신 프로필 ID: " + profileId);
    }
}
