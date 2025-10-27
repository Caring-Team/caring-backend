package com.caring.caringbackend.domain.auth.controller;

import com.caring.caringbackend.domain.auth.dto.request.OAuthLoginRequest;
import com.caring.caringbackend.domain.auth.dto.request.SendCertificationCodeRequest;
import com.caring.caringbackend.domain.auth.dto.request.UserLocalLoginRequest;
import com.caring.caringbackend.domain.auth.dto.request.UserLocalRegisterRequest;
import com.caring.caringbackend.domain.auth.dto.request.UserOAuth2RegisterRequest;
import com.caring.caringbackend.domain.auth.dto.request.VerifyPhoneRequest;
import com.caring.caringbackend.domain.auth.dto.request.TokenRefreshRequest;
import com.caring.caringbackend.domain.auth.dto.response.JwtTokenResponse;
import com.caring.caringbackend.domain.auth.service.AuthService;
import com.caring.caringbackend.global.jwt.details.TemporaryUserDetails;
import com.caring.caringbackend.global.jwt.details.MemberDetails;
import com.caring.caringbackend.global.security.details.TemporaryUserDetails;
import com.caring.caringbackend.global.security.details.MemberDetails;
import com.caring.caringbackend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * ID/PW 회원가입: 전화번호 인증 > ID/PW + 추가정보 입력 전화번호 인증 1. 전화번호가 DB에 있는 경우 (1) ID/PW로 이미 회원가입한 경우 이미 가입한 회원. 아이디/비밀번호 찾기로 유도한다.
 * (2) OAuth로 이미 회원가입한 경우 OAuth 계정과 연동을 진행한다. (3) 휴대전화 번호 변경 등 다른 사용자가 전화번호로 회원가입한 경우 기존 계정 비활성화 후 신규 회원가입 진행한다. 2.
 * 전화번호가 DB에 없는 경우 회원가입을 진행한다. OAuth 회원가입: OAuth 로그인 > 전화번호 인증 > 추가정보 입력 1. 전화번호가 DB에 있는 경우 (1) ID/PW 또는 OAuth로 이미 회원가입한
 * 경우 OAuth 계정과 연동을 진행한다. (2) 휴대전화 번호 변경 등 다론사용자가 전화번호로 회원가입한 경우 기존 계정 비활성화 후 신규 회원가입 진행한다.
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    // MEMBER GLOBAL ENDPOINTS

    @PostMapping("/token/refresh")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> refreshAccessTokenMember(
            @Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        JwtTokenResponse jwtTokenResponse = authService.regenerateAccessTokenMember(tokenRefreshRequest);
        return ApiResponse.success(ResponseEntity.ok(jwtTokenResponse));
    }

    // OAUTH ENDPOINTS

    @PostMapping(value = "/oauth2/login/{provider}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ResponseEntity<JwtTokenResponse>> loginMemberOAuth2(
            @PathVariable String provider,
            @Valid @RequestBody OAuthLoginRequest request) {
        JwtTokenResponse jwtTokenResponse = authService.oAuth2LoginOrGenerateTemporaryToken(provider, request);
        return ApiResponse.success(ResponseEntity.ok(jwtTokenResponse));
    }

    @PreAuthorize("hasRole('TEMP_OAUTH')")
    @PostMapping("/oauth2/certification-code")
    public ApiResponse<ResponseEntity<Boolean>> sendCertificationCodeOAuth2(
            @Valid @RequestBody SendCertificationCodeRequest certificationCodeRequest) {
        authService.sendCertificationCode(certificationCodeRequest);
        return ApiResponse.success(ResponseEntity.ok(true));
    }

    @PreAuthorize("hasRole('TEMP_OAUTH')")
    @PostMapping("/oauth2/verify-phone")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> verifyPhoneOAuth2(
            @AuthenticationPrincipal TemporaryUserDetails temporaryUserDetails,
            @Valid @RequestBody VerifyPhoneRequest request) {
        return ApiResponse.success(
                ResponseEntity.ok(authService.verifyPhoneOAuth2(temporaryUserDetails, request)));
    }

    @PreAuthorize("hasRole('TEMP_OAUTH')")
    @PostMapping("/oauth2/register")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> completeRegisterOAuth2(
            @AuthenticationPrincipal TemporaryUserDetails temporaryUserDetails,
            @Valid @RequestBody UserOAuth2RegisterRequest registerRequest) {
        return ApiResponse.success(ResponseEntity.ok(
                authService.completeRegisterOAuth2(temporaryUserDetails, registerRequest)));
    }

    // LOCAL ENDPOINTS

    @PostMapping("/login")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> loginMemberLocal(
            @Valid @RequestBody UserLocalLoginRequest request) {
        return ApiResponse.success(ResponseEntity.ok(authService.loginMemberLocal(request)));
    }

    @PostMapping("/certification-code")
    public ApiResponse<ResponseEntity<Boolean>> sendCertificationCodeMemberLocal(
            @Valid @RequestBody SendCertificationCodeRequest certificationCodeRequest) {
        authService.sendCertificationCode(certificationCodeRequest);
        return ApiResponse.success(ResponseEntity.ok(true));
    }

    @PostMapping("/verify-phone")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> verifyPhoneMemberLocal(
            @Valid @RequestBody VerifyPhoneRequest request) {

        return ApiResponse.success(ResponseEntity.ok(authService.verifyPhoneNumberLocal(request)));
    }

    @PreAuthorize("hasRole('TEMP_LOCAL')")
    @PostMapping("/register")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> completeRegisterMemberLocal(
            @AuthenticationPrincipal TemporaryUserDetails temporaryUserDetails,
            @Valid @RequestBody UserLocalRegisterRequest request) {

        return ApiResponse.success(ResponseEntity.ok(authService.completeRegisterLocal(temporaryUserDetails, request)));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add-local")
    public ApiResponse<ResponseEntity<Boolean>> addLocalCredential(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody UserLocalRegisterRequest request) {

        return ApiResponse.success(ResponseEntity.ok(authService.addLocalCredential(memberDetails, request)));
    }
}
