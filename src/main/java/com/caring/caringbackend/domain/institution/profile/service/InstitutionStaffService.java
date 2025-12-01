package com.caring.caringbackend.domain.institution.profile.service;

import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionStaffsResponse;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class InstitutionStaffService {

    private final InstitutionAdminRepository institutionAdminRepository;

    @Transactional(readOnly = true)
    public InstitutionStaffsResponse getAllStaffs(Long adminId) {
        InstitutionAdmin admin = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        Institution institution = Optional.ofNullable(admin.getInstitution())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION));

        List<InstitutionAdmin> staffs = institutionAdminRepository.findAllByInstitution(institution);

        return InstitutionStaffsResponse.from(staffs);
    }


    @Transactional
    public void removeInstitutionAdminFromInstitution(Long adminId, Long staffId) {
        if (adminId.equals(staffId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        InstitutionAdmin admin = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        Institution institution = Optional.ofNullable(admin.getInstitution())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION));

        InstitutionAdmin staff = institutionAdminRepository.findById(staffId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        if (!institution.equals(staff.getInstitution())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        staff.unlinkInstitution();
    }
}
