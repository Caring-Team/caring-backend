package com.caring.caringbackend.domain.auth.dto.response;

import com.caring.caringbackend.domain.user.guardian.entity.CredentialType;

/**
 * Provider로부터 제공받은 AccessToken으로 유저 정보를 요청 후 받아오는 DTO의 공통 인터페이스 <br>
 * <br>
 * 포함한 정보 <br>
 * <code>ProviderType</code>: Google, Naver, Kakao, ...  컨트롤러에서 각 Proivder별 서비스를 찾는데 사용. <br>
 * <code>UserId</code>: Provider로부터 제공받은 UserId      Member entity에 저장하여 로그인 시 사용. <br>
 * <code>Name</code>: 사용자 이름                         기본적인 사용자 이름. <br>
 * <br>
 * 포함하지 않은 정보 <br>
 * <code>Email</code>: Kakao는 Email보다 전화번호로 회원가입하는 경우가 많아 Email 필드가 비어있는 경우가 발생함.
 */
public interface OAuth2ProviderUserInfoResponse {

    public CredentialType getProviderType();

    public String getUserId();

    public String getName();
}
