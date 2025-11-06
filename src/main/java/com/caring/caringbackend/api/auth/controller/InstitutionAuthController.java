package com.caring.caringbackend.api.auth.controller;

import com.caring.caringbackend.api.auth.dto.request.SendCertificationCodeRequest;
import com.caring.caringbackend.api.auth.dto.request.TokenRefreshRequest;
import com.caring.caringbackend.api.auth.dto.request.VerifyPhoneRequest;
import com.caring.caringbackend.api.auth.dto.request.institution.local.InstitutionLocalLoginRequest;
import com.caring.caringbackend.api.auth.dto.request.institution.local.InstitutionLocalRegisterRequest;
import com.caring.caringbackend.domain.auth.service.AuthService;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import com.caring.caringbackend.global.security.details.TemporaryInstitutionAdminDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/institution")
@RestController
public class InstitutionAuthController {

    private final AuthService authService;

    @PostMapping("/certification-code")
    public ResponseEntity<?> sendCertificationCodeInstitutionAdmin(
            @Valid @RequestBody SendCertificationCodeRequest sendCertificationCodeRequest) {

        authService.sendCertificationCode(sendCertificationCodeRequest);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<?> verifyPhoneInstitutionAdmin(
            @Valid @RequestBody VerifyPhoneRequest verifyPhoneRequest) {

        return ResponseEntity.ok().headers(authService.verifyPhoneInstitution(verifyPhoneRequest).toHeaders()).build();
    }

    @PreAuthorize("hasRole('TEMP_INSTITUTION')")
    @PostMapping("/register")
    public ResponseEntity<?> completeRegisterInstitutionAdmin(
            @AuthenticationPrincipal TemporaryInstitutionAdminDetails temporaryInstitutionDetails,
            @Valid @RequestBody InstitutionLocalRegisterRequest institutionLocalRegisterRequest) {

        return ResponseEntity.ok().headers(
                authService.completeRegisterInstitution(temporaryInstitutionDetails, institutionLocalRegisterRequest)
                        .toHeaders()).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginInstitutionAdmin(
            @Valid @RequestBody InstitutionLocalLoginRequest institutionLocalLoginRequest) {

        return ResponseEntity.ok().headers(authService.loginInstitutionAdmin(institutionLocalLoginRequest).toHeaders())
                .build();
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshAccessTokenInstitutionAdmin(
            @CookieValue(value = "refresh_token", required = false) String refreshToken) {

        TokenRefreshRequest dto = TokenRefreshRequest.builder().requestToken(refreshToken).build();
        return ResponseEntity.ok()
                .headers(authService.regenerateAccessTokenInstitutionAdmin(dto).toHeaders()).build();
    }

    @PreAuthorize("hasAnyRole('INSTITUTION_STAFF', 'INSTITUTION_OWNER')")
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(
            @AuthenticationPrincipal InstitutionAdminDetails institutionAdminDetails) {
        return ResponseEntity.ok(authService.getInstitutionAdminInformation(institutionAdminDetails));
    }
}
