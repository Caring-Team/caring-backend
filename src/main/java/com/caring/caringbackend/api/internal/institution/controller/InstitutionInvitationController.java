package com.caring.caringbackend.api.internal.institution.controller;

import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionInvitationSendRequestDto;
import com.caring.caringbackend.domain.institution.invitation.service.InstitutionInvitationService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@PreAuthorize("hasRole('INSTITUTION_OWNER')")
@RestController
@RequestMapping("/api/v1/institutions/me/invitations")
public class InstitutionInvitationController {

    private final InstitutionInvitationService institutionInvitationService;

    @GetMapping("")
    public ApiResponse<?> getInvitations(@AuthenticationPrincipal InstitutionAdminDetails adminDetails) {
        return ApiResponse.success(institutionInvitationService.getInstitutionInvitations(adminDetails.getId()));
    }

    @PostMapping("")
    public ApiResponse<Void> sendInvitation(@AuthenticationPrincipal InstitutionAdminDetails adminDetails,
                                            @Valid @RequestBody InstitutionInvitationSendRequestDto sendRequestDto) {

        institutionInvitationService
                .sendInstitutionInvitation(adminDetails.getId(), sendRequestDto.getUsername());

        return ApiResponse.success();
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancelInvitation(@AuthenticationPrincipal InstitutionAdminDetails adminDetails,
                                              @PathVariable("id") Long invitationId) {
        institutionInvitationService.cancelInstitutionInvitation(adminDetails.getId(), invitationId);
        return ApiResponse.success();
    }

}
