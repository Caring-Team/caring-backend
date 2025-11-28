package com.caring.caringbackend.api.institutionadmin.controller;

import com.caring.caringbackend.domain.institution.invitation.entity.enums.InstitutionInvitationStatus;
import com.caring.caringbackend.domain.institution.invitation.service.InstitutionInvitationService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@PreAuthorize("hasRole('INSTITUTION_STAFF')")
@RestController
@RequestMapping("/api/v1/institution-admins/me/invitations")
public class InstitutionAdminInvitationController {

    private final InstitutionInvitationService institutionInvitationService;

    @GetMapping("")
    public ApiResponse<?> getInvitations(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @RequestParam(value = "status", required = false) InstitutionInvitationStatus status) {
        if (status != null) {
            return ApiResponse.success(
                    institutionInvitationService.getInstitutionAdminInvitations(adminDetails.getId(), status)
            );
        } else {
            return ApiResponse.success(
                    institutionInvitationService.getInstitutionAdminInvitations(adminDetails.getId())
            );
        }
    }

    @PostMapping("/{id}/accept")
    public ApiResponse<Void> acceptInvitation(@AuthenticationPrincipal InstitutionAdminDetails adminDetails,
                                              @PathVariable("id") Long invitationId) {
        institutionInvitationService.acceptInstitutionInvitation(adminDetails.getId(), invitationId);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<Void> rejectInvitation(@AuthenticationPrincipal InstitutionAdminDetails adminDetails,
                                              @PathVariable("id") Long invitationId) {

        institutionInvitationService.rejectInstitutionInvitation(adminDetails.getId(), invitationId);
        return ApiResponse.success();
    }

}
