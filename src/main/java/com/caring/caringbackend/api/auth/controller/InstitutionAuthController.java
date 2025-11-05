package com.caring.caringbackend.api.auth.controller;

import com.caring.caringbackend.api.auth.dto.request.SendCertificationCodeRequest;
import com.caring.caringbackend.api.auth.dto.request.TokenRefreshRequest;
import com.caring.caringbackend.api.auth.dto.request.VerifyPhoneRequest;
import com.caring.caringbackend.api.auth.dto.request.institution.local.InstitutionLocalLoginRequest;
import com.caring.caringbackend.api.auth.dto.request.institution.local.InstitutionLocalRegisterRequest;
import com.caring.caringbackend.api.auth.dto.response.JwtTokenResponse;
import com.caring.caringbackend.domain.auth.service.AuthService;
import com.caring.caringbackend.global.security.details.TemporaryInstitutionAdminDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Boolean> sendCertificationCodeInstitutionAdmin(
            @Valid @RequestBody SendCertificationCodeRequest sendCertificationCodeRequest) {

        authService.sendCertificationCode(sendCertificationCodeRequest);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<JwtTokenResponse> verifyPhoneInstitutionAdmin(
            @Valid @RequestBody VerifyPhoneRequest verifyPhoneRequest) {

        return ResponseEntity.ok(authService.verifyPhoneInstitution(verifyPhoneRequest));
    }

    @PreAuthorize("hasRole('TEMP_INSTITUTION')")
    @PostMapping("/register")
    public ResponseEntity<JwtTokenResponse> completeRegisterInstitutionAdmin(
            @AuthenticationPrincipal TemporaryInstitutionAdminDetails temporaryInstitutionDetails,
            @Valid @RequestBody InstitutionLocalRegisterRequest institutionLocalRegisterRequest) {

        return ResponseEntity.ok(
                authService.completeRegisterInstitution(temporaryInstitutionDetails, institutionLocalRegisterRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> loginInstitutionAdmin(
            @Valid @RequestBody InstitutionLocalLoginRequest institutionLocalLoginRequest) {

        return ResponseEntity.ok(authService.loginInstitutionAdmin(institutionLocalLoginRequest));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<JwtTokenResponse> refreshAccessTokenInstitutionAdmin(
            @Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {

        return ResponseEntity.ok(authService.regenerateAccessTokenInstitutionAdmin(tokenRefreshRequest));
    }

}
