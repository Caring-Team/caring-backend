package com.caring.caringbackend.domain.auth.controller;

import com.caring.caringbackend.domain.auth.dto.request.InstitutionLocalLoginRequest;
import com.caring.caringbackend.domain.auth.dto.request.InstitutionLocalRegisterRequest;
import com.caring.caringbackend.domain.auth.dto.request.OAuthLoginRequest;
import com.caring.caringbackend.domain.auth.dto.request.SendCertificationCodeRequest;
import com.caring.caringbackend.domain.auth.dto.request.UserLocalLoginRequest;
import com.caring.caringbackend.domain.auth.dto.request.UserLocalRegisterRequest;
import com.caring.caringbackend.domain.auth.dto.request.UserOAuth2RegisterRequest;
import com.caring.caringbackend.domain.auth.dto.request.VerifyPhoneRequest;
import com.caring.caringbackend.domain.auth.dto.request.TokenRefreshRequest;
import com.caring.caringbackend.domain.auth.dto.response.JwtTokenResponse;
import com.caring.caringbackend.domain.auth.service.AuthService;
import com.caring.caringbackend.global.security.details.TemporaryInstitutionAdminDetails;
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

    /**
     * OAuth2 로그인을 한다.
     *
     * @param provider google, naver, kakao
     * @param request  authentication code
     * @return 해당 Provider로 계정이 있는 경우: <code>Fully jwt</code>> 해당 Provider로 계정이 없는 경우: 임시 <code>Access token</code>
     */
    @PostMapping(value = "/oauth2/login/{provider}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ResponseEntity<JwtTokenResponse>> loginMemberOAuth2(
            @PathVariable String provider,
            @Valid @RequestBody OAuthLoginRequest request) {
        JwtTokenResponse jwtTokenResponse = authService.oAuth2LoginOrGenerateTemporaryToken(provider, request);
        return ApiResponse.success(ResponseEntity.ok(jwtTokenResponse));
    }

    /**
     * OAuth2 회원가입 흐름에서 전화번호에 인증번호를 보낸다.
     *
     * @param certificationCodeRequest 이름, 생년월일, 전화번호
     * @return true
     */
    @PreAuthorize("hasRole('TEMP_OAUTH')")
    @PostMapping("/oauth2/certification-code")
    public ApiResponse<ResponseEntity<Boolean>> sendCertificationCodeOAuth2(
            @Valid @RequestBody SendCertificationCodeRequest certificationCodeRequest) {
        authService.sendCertificationCode(certificationCodeRequest);
        return ApiResponse.success(ResponseEntity.ok(true));
    }

    /**
     * OAuth2 회원가입 흐름에서 전화번호를 인증한다.
     *
     * @param request 이름, 생년월일, 전화번호, 인증번호
     * @return 이미 계정이 존재하는 경우: 연동 후 <code>Fully jwt</code> <br> 계정이 존재하지 않은 경우: 기존 임시 <code>Access token</code>
     */
    @PreAuthorize("hasRole('TEMP_OAUTH')")
    @PostMapping("/oauth2/verify-phone")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> verifyPhoneOAuth2(
            @AuthenticationPrincipal TemporaryUserDetails temporaryUserDetails,
            @Valid @RequestBody VerifyPhoneRequest request) {
        return ApiResponse.success(
                ResponseEntity.ok(authService.verifyPhoneOAuth2(temporaryUserDetails, request)));
    }

    /**
     * OAuth2 회원가입 흐름에서 기본 정보를 입력한다.
     *
     * @param registerRequest 성별, 주소
     * @return <code>Fully jwt</code>
     */
    @PreAuthorize("hasRole('TEMP_OAUTH')")
    @PostMapping("/oauth2/register")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> completeRegisterOAuth2(
            @AuthenticationPrincipal TemporaryUserDetails temporaryUserDetails,
            @Valid @RequestBody UserOAuth2RegisterRequest registerRequest) {
        return ApiResponse.success(ResponseEntity.ok(
                authService.completeRegisterOAuth2(temporaryUserDetails, registerRequest)));
    }

    // LOCAL ENDPOINTS

    /**
     * local 로그인
     *
     * @param request id, password
     * @return <code>Fully jwt</code>
     */
    @PostMapping("/login")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> loginMemberLocal(
            @Valid @RequestBody UserLocalLoginRequest request) {
        return ApiResponse.success(ResponseEntity.ok(authService.loginMemberLocal(request)));
    }

    /**
     * Local 회원가입 흐름에서 전화번호에 인증번호를 보낸다.
     *
     * @param certificationCodeRequest 이름, 생년월일, 전화번호
     * @return true
     */
    @PostMapping("/certification-code")
    public ApiResponse<ResponseEntity<Boolean>> sendCertificationCodeMemberLocal(
            @Valid @RequestBody SendCertificationCodeRequest certificationCodeRequest) {
        authService.sendCertificationCode(certificationCodeRequest);
        return ApiResponse.success(ResponseEntity.ok(true));
    }

    /**
     * Local 회원가입 흐름에서 전화번호를 인증한다.
     *
     * @param request 이름, 생년월일, 전화번호, 인증번호
     * @return 기존 소셜 계정이 있는 경우: 해당 Member 권한의 <code>Fully jwt</code> <br>기존 소셜 계정이 없는 경우: 임시 <code>Access token</code>
     */
    @PostMapping("/verify-phone")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> verifyPhoneMemberLocal(
            @Valid @RequestBody VerifyPhoneRequest request) {

        return ApiResponse.success(ResponseEntity.ok(authService.verifyPhoneNumberLocal(request)));
    }

    /**
     * Local 회원가입을 완료한다
     *
     * @param request ID, PW, 성별, 주소
     * @return <code>Fully jwt</code>
     */
    @PreAuthorize("hasRole('TEMP_LOCAL')")
    @PostMapping("/register")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> completeRegisterMemberLocal(
            @AuthenticationPrincipal TemporaryUserDetails temporaryUserDetails,
            @Valid @RequestBody UserLocalRegisterRequest request) {

        return ApiResponse.success(ResponseEntity.ok(authService.completeRegisterLocal(temporaryUserDetails, request)));
    }


    /**
     * OAuth2로 이미 회원가입 된 유저의 ID/PW를 추가한다.
     *
     * @param request id, pw
     * @return ture
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add-local")
    public ApiResponse<ResponseEntity<Boolean>> addLocalCredential(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody UserLocalRegisterRequest request) {

        return ApiResponse.success(ResponseEntity.ok(authService.addLocalCredential(memberDetails, request)));
    }

    // INSTITUTION ENDPOINTS

    @PostMapping("/institution/certification-code")
    public ApiResponse<ResponseEntity<Boolean>> sendCertificationCodeInstitutionAdmin(
            @Valid @RequestBody SendCertificationCodeRequest request) {

        authService.sendCertificationCode(request);
        return ApiResponse.success(ResponseEntity.ok(true));
    }

    @PostMapping("/institution/verify-phone")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> verifyPhoneInstitutionAdmin(
            @Valid @RequestBody VerifyPhoneRequest request) {

        return ApiResponse.success(ResponseEntity.ok(authService.verifyPhoneInstitution(request)));
    }

    @PreAuthorize("hasRole('TEMP_INSTITUTION')")
    @PostMapping("/institution/register")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> completeRegisterInstitutionAdmin(
            @AuthenticationPrincipal TemporaryInstitutionAdminDetails temporaryInstitutionDetails,
            @Valid @RequestBody InstitutionLocalRegisterRequest registerRequest) {

        return ApiResponse.success(ResponseEntity.ok(
                authService.completeRegisterInstitution(temporaryInstitutionDetails, registerRequest)));
    }

    @PostMapping("/institution/login")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> loginInstitutionAdmin(
            @Valid @RequestBody InstitutionLocalLoginRequest request) {

        return ApiResponse.success(ResponseEntity.ok(authService.loginInstitutionAdmin(request)));
    }

    @PostMapping("/institution/token/refresh")
    public ApiResponse<ResponseEntity<JwtTokenResponse>> refreshAccessTokenInstitutionAdmin(
            @Valid @RequestBody TokenRefreshRequest request) {

        return ApiResponse.success(ResponseEntity.ok(authService.regenerateAccessTokenInstitutionAdmin(request)));
    }

    /*
    TODO: LOGOUT
    Refresh token 을 ID로 redis 에 저장: 중복 로그인이 가능, 로그아웃 시 요청한 클라이언트를 구분할 방법이 필요함
    Device ID는 접근 불가능 > Application 최초 구동 또는 로그인 시 State를 생성 후 요청마다 서버에 전송

    User id 를 ID로 redis 에 저장: 중복 로그인 불가능, 로그아웃 시 User id로 간단하게 로그아웃 할 수 있음

   중복 로그인이 필요한가?
    */
}
