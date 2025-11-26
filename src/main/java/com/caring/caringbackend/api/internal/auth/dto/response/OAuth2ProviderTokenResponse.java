package com.caring.caringbackend.api.internal.auth.dto.response;

/**
 * Provider에게 Token을 요청 후 받아오는 DTO의 공통 인터페이스 <br>
 * <br>
 * 포함한 정보 <br>
 * <code>AccessToken</code>: Provider에게 기본적인 UserInfo를 받아오기 위해 필요함. <br>
 * <br>
 * 포함하지 않은 정보 <br>
 * <code>RefreshToken</code>: RefreshToken 사용 대신 다시 OAuth2 로그인을 진행해야함.
 */
public interface OAuth2ProviderTokenResponse {

    public String getAccessToken();
}
