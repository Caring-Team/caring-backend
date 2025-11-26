package com.caring.caringbackend.api.auth.controller;

import com.caring.caringbackend.api.auth.dto.request.SendCertificationCodeRequest;
import com.caring.caringbackend.api.auth.dto.request.TokenRefreshRequest;
import com.caring.caringbackend.api.auth.dto.request.VerifyPhoneRequest;
import com.caring.caringbackend.api.auth.dto.request.user.local.UserLocalLoginRequest;
import com.caring.caringbackend.api.auth.dto.request.user.local.UserLocalRegisterRequest;
import com.caring.caringbackend.api.auth.dto.request.user.oauth.UserOAuth2LoginRequest;
import com.caring.caringbackend.api.auth.dto.request.user.oauth.UserOAuth2RegisterRequest;
import com.caring.caringbackend.api.auth.dto.response.JwtTokenResponse;
import com.caring.caringbackend.domain.auth.service.AuthService;
import com.caring.caringbackend.global.security.details.MemberDetails;
import com.caring.caringbackend.global.security.details.TemporaryUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
@Tag(name = "🧑‍🤝‍🧑 Member Auth", description = "회원 인증 API")
public class MemberAuthController {

    private final AuthService authService;

    /**
     * Local 회원가입 흐름에서 전화번호에 인증번호를 보낸다.
     *
     * @param sendCertificationCodeRequest 이름, 생년월일, 전화번호
     * @return true
     */
    @PostMapping("/certification-code")
    @Operation(summary = "1. 회원 휴대폰 인증 코드 전송", description = "회원 휴대폰 번호로 인증 코드를 전송합니다.")
    public ResponseEntity<Boolean> sendCertificationCodeMemberLocal(
            @Valid @RequestBody SendCertificationCodeRequest sendCertificationCodeRequest) {
        authService.sendCertificationCode(sendCertificationCodeRequest);
        return ResponseEntity.ok(true);
    }

    /**
     * Local 회원가입 흐름에서 전화번호를 인증한다.
     *
     * @param verifyPhoneRequest 이름, 생년월일, 전화번호, 인증번호
     * @return 기존 소셜 계정이 있는 경우: 해당 Member 권한의 <code>Fully jwt</code> <br>기존 소셜 계정이 없는 경우: 임시 <code>Access token</code>
     */
    @PostMapping("/verify-phone")
    @Operation(summary = "2. 회원 휴대폰 번호 인증", description = "회원 휴대폰 번호와 인증 코드를 검증하여 임시 회원 권한을 부여합니다.")
    public ResponseEntity<JwtTokenResponse> verifyPhoneMemberLocal(
            @Valid @RequestBody VerifyPhoneRequest verifyPhoneRequest) {

        return ResponseEntity.ok(authService.verifyPhoneNumberLocal(verifyPhoneRequest));
    }

    /**
     * Local 회원가입을 완료한다
     *
     * @param userLocalRegisterRequest ID, PW, 성별, 주소
     * @return <code>Fully jwt</code>
     */
    @PreAuthorize("hasRole('TEMP_LOCAL')")
    @PostMapping("/register")
    @Operation(summary = "3. 회원 가입 완료", description = "임시 회원 권한을 가진 사용자가 회원가입을 완료합니다.")
    public ResponseEntity<JwtTokenResponse> completeRegisterMemberLocal(
            @AuthenticationPrincipal TemporaryUserDetails temporaryUserDetails,
            @Valid @RequestBody UserLocalRegisterRequest userLocalRegisterRequest) {

        return ResponseEntity.ok(authService.completeRegisterLocal(temporaryUserDetails, userLocalRegisterRequest));
    }

    /**
     * local 로그인
     *
     * @param userLocalLoginRequest id, password
     * @return <code>Fully jwt</code>
     */
    @PostMapping("/login")
    @Operation(summary = "4. 회원 로그인", description = "회원 로컬 로그인 처리")
    public ResponseEntity<JwtTokenResponse> loginMemberLocal(
            @Valid @RequestBody UserLocalLoginRequest userLocalLoginRequest) {
        return ResponseEntity.ok(authService.loginMemberLocal(userLocalLoginRequest));
    }

    /**
     * OAuth2 로그인을 한다.
     *
     * @param provider               google, naver, kakao
     * @param userOAuth2LoginRequest authentication code
     * @return 해당 Provider로 계정이 있는 경우: <code>Fully jwt</code>> 해당 Provider로 계정이 없는 경우: 임시 <code>Access token</code>
     */
    @PostMapping(value = "/oauth2/login/{provider}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "5. OAuth2 회원 로그인", description = "OAuth2 회원 로그인 처리")
    public ResponseEntity<JwtTokenResponse> loginMemberOAuth2(
            @PathVariable String provider,
            @Valid @RequestBody UserOAuth2LoginRequest userOAuth2LoginRequest) {
        JwtTokenResponse jwtTokenResponse = authService.
                oAuth2LoginOrGenerateTemporaryToken(provider, userOAuth2LoginRequest);
        return ResponseEntity.ok(jwtTokenResponse);
    }

    /**
     * OAuth2 회원가입 흐름에서 전화번호에 인증번호를 보낸다.
     *
     * @param sendCertificationCodeRequest 이름, 생년월일, 전화번호
     * @return true
     */
    @PreAuthorize("hasRole('TEMP_OAUTH')")
    @PostMapping("/oauth2/certification-code")
    @Operation(summary = "6. OAuth2 회원 휴대폰 인증 코드 전송", description = "OAuth2 회원 휴대폰 번호로 인증 코드를 전송합니다.")
    public ResponseEntity<Boolean> sendCertificationCodeOAuth2(
            @Valid @RequestBody SendCertificationCodeRequest sendCertificationCodeRequest) {
        authService.sendCertificationCode(sendCertificationCodeRequest);
        return ResponseEntity.ok(true);
    }

    /**
     * OAuth2 회원가입 흐름에서 전화번호를 인증한다.
     *
     * @param verifyPhoneRequest 이름, 생년월일, 전화번호, 인증번호
     * @return 이미 계정이 존재하는 경우: 연동 후 <code>Fully jwt</code> <br> 계정이 존재하지 않은 경우: 기존 임시 <code>Access token</code>
     */
    @PreAuthorize("hasRole('TEMP_OAUTH')")
    @PostMapping("/oauth2/verify-phone")
    @Operation(summary = "7. OAuth2 회원 휴대폰 번호 인증", description = "OAuth2 회원 휴대폰 번호와 인증 코드를 검증하여 기존 계정과 연동하거나 계속해서 회원가입을 진행합니다.")
    public ResponseEntity<JwtTokenResponse> verifyPhoneOAuth2(
            @AuthenticationPrincipal TemporaryUserDetails temporaryUserDetails,
            @Valid @RequestBody VerifyPhoneRequest verifyPhoneRequest) {
        return ResponseEntity.ok(authService.verifyPhoneOAuth2(temporaryUserDetails, verifyPhoneRequest));
    }

    /**
     * OAuth2 회원가입 흐름에서 기본 정보를 입력한다.
     *
     * @param userOAuth2RegisterRequest 성별, 주소
     * @return <code>Fully jwt</code>
     */
    @PreAuthorize("hasRole('TEMP_OAUTH')")
    @PostMapping("/oauth2/register")
    @Operation(summary = "8. OAuth2 회원 가입 완료", description = "임시 OAuth2 회원 권한을 가진 사용자가 회원가입을 완료합니다.")
    public ResponseEntity<JwtTokenResponse> completeRegisterOAuth2(
            @AuthenticationPrincipal TemporaryUserDetails temporaryUserDetails,
            @Valid @RequestBody UserOAuth2RegisterRequest userOAuth2RegisterRequest) {
        return ResponseEntity.ok(
                authService.completeRegisterOAuth2(temporaryUserDetails, userOAuth2RegisterRequest));
    }

    /**
     * OAuth2로 이미 회원가입 된 유저의 ID/PW를 추가한다.
     *
     * @param userLocalRegisterRequest id, pw
     * @return ture
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/oauth2/link-local")
    @Operation(summary = "9. OAuth2 회원 로컬 계정 연동", description = "OAuth2로 이미 회원가입 된 사용자가 로컬 계정을 추가합니다.")
    public ResponseEntity<Boolean> addLocalCredential(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody UserLocalRegisterRequest userLocalRegisterRequest) {

        return ResponseEntity.ok(authService.addLocalCredential(memberDetails, userLocalRegisterRequest));
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "10. 회원 액세스 토큰 재발급", description = "회원 액세스 토큰을 재발급합니다.")
    public ResponseEntity<JwtTokenResponse> refreshAccessTokenMember(
            @Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        JwtTokenResponse jwtTokenResponse = authService.regenerateAccessTokenMember(tokenRefreshRequest);
        return ResponseEntity.ok(jwtTokenResponse);
    }
}
