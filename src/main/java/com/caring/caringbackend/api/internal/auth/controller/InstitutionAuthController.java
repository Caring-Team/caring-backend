package com.caring.caringbackend.api.internal.auth.controller;

import com.caring.caringbackend.api.internal.auth.dto.request.SendCertificationCodeRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.TokenRefreshRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.VerifyPhoneRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.institution.local.InstitutionLocalLoginRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.institution.local.InstitutionLocalRegisterRequest;
import com.caring.caringbackend.domain.auth.service.AuthService;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import com.caring.caringbackend.global.security.details.TemporaryInstitutionAdminDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/institution")
@RestController
@Tag(name = "02. 🏥 Institution Auth", description = "기관 인증 API | 기관 회원가입, 로그인, 토큰 관리")
public class InstitutionAuthController {

    private final AuthService authService;

    @PostMapping("/certification-code")
    @Operation(summary = "1. 기관 관리자 휴대폰 인증 코드 전송", description = "기관 관리자 휴대폰 번호로 인증 코드를 전송합니다.")
    public ResponseEntity<?> sendCertificationCodeInstitutionAdmin(
            @Valid @RequestBody SendCertificationCodeRequest sendCertificationCodeRequest) {

        authService.sendCertificationCode(sendCertificationCodeRequest);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/verify-phone")
    @Operation(summary = "2. 기관 관리자 휴대폰 번호 인증", description = "기관 관리자 휴대폰 번호와 인증 코드를 검증하여 임시 기관 관리자 권한을 부여합니다.")
    public ResponseEntity<?> verifyPhoneInstitutionAdmin(
            @Valid @RequestBody VerifyPhoneRequest verifyPhoneRequest) {

        return ResponseEntity.ok(authService.verifyPhoneInstitution(verifyPhoneRequest));
    }

    @PreAuthorize("hasRole('TEMP_INSTITUTION')")
    @PostMapping("/register")
    @Operation(summary = "3. 기관 관리자 회원가입 완료", description = "임시 기관 관리자 권한을 가진 사용자가 회원가입을 완료합니다.")
    public ResponseEntity<?> completeRegisterInstitutionAdmin(
            @AuthenticationPrincipal TemporaryInstitutionAdminDetails temporaryInstitutionDetails,
            @Valid @RequestBody InstitutionLocalRegisterRequest institutionLocalRegisterRequest) {

        return ResponseEntity.ok(
                authService.completeRegisterInstitution(temporaryInstitutionDetails, institutionLocalRegisterRequest));
    }

    @PostMapping("/login")
    @Operation(summary = "4. 기관 관리자 로그인", description = "기관 관리자 로컬 로그인 처리")
    public ResponseEntity<?> loginInstitutionAdmin(
            @Valid @RequestBody InstitutionLocalLoginRequest institutionLocalLoginRequest) {

        return ResponseEntity.ok(authService.loginInstitutionAdmin(institutionLocalLoginRequest));
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "5. 기관 관리자 액세스 토큰 재발급", description = "기관 관리자 액세스 토큰을 재발급합니다.")
    public ResponseEntity<?> refreshAccessTokenInstitutionAdmin(
            @Valid @RequestBody TokenRefreshRequest request) {

        TokenRefreshRequest dto = TokenRefreshRequest.builder().requestToken(request.getRequestToken()).build();
        return ResponseEntity.ok(authService.regenerateAccessTokenInstitutionAdmin(dto));
    }

    @PreAuthorize("hasAnyRole('INSTITUTION_STAFF', 'INSTITUTION_OWNER')")
    @GetMapping("/me")
    @Operation(summary = "6. 기관 관리자 내 정보 조회", description = "인증된 기관 관리자의 내 정보를 조회합니다.")
    public ResponseEntity<?> getMyInfo(
            @AuthenticationPrincipal InstitutionAdminDetails institutionAdminDetails) {
        return ResponseEntity.ok(authService.getInstitutionAdminInformation(institutionAdminDetails));
    }
}
