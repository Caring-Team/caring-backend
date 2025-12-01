package com.caring.caringbackend.api.internal.institution.controller;


import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionStaffsResponse;
import com.caring.caringbackend.domain.institution.profile.service.InstitutionStaffService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('INSTITUTION_OWNER')")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/institutions/me/staffs")
public class InstitutionStaffController {


    private final InstitutionStaffService institutionStaffService;

    @GetMapping()
    public ApiResponse<InstitutionStaffsResponse> getAllStaffs(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails) {
        return ApiResponse.success(institutionStaffService.getAllStaffs(adminDetails.getId()));
    }

    @DeleteMapping("/{staffId}")
    public ApiResponse<Void> removeStaffFromInstitution(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable(value = "staffId") Long staffId) {
        institutionStaffService.removeInstitutionAdminFromInstitution(adminDetails.getId(), staffId);
        return ApiResponse.success();
    }
}
