package com.caring.caringbackend.domain.institution.profile.service;

import com.caring.caringbackend.api.institution.dto.request.CareGiverCreateRequestDto;
import com.caring.caringbackend.domain.institution.profile.entity.CareGiver;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.CareGiverRepository;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CareGiverServiceImpl implements CareGiverService {

    private final CareGiverRepository careGiverRepository;
    private final InstitutionAdminRepository institutionAdminRepository;
    private final InstitutionRepository institutionRepository;

    @Override
    public void registerCareGiver(
            Long adminId, Long institutionId,
            CareGiverCreateRequestDto requestDto) {

        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(institutionId, admin);

        Institution institution = findInstitutionById(institutionId);

        CareGiver careGiver = CareGiver.createCareGiver(
                institution,
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPhoneNumber(),
                requestDto.getGender(),
                requestDto.getBirthDate(),
                requestDto.getExperienceDetails()
        );
        institution.addCareGiver(careGiver);
        careGiverRepository.save(careGiver);
    }

    private void validate(Long institutionId, InstitutionAdmin admin) {
        if (!admin.hasInstitution()) {
            throw new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION);
        }

        if (!admin.belongsToInstitution(institutionId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_INSTITUTION_ACCESS);
        }
    }

    private Institution findInstitutionById(Long institutionId) {
        return institutionRepository.findById(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND));
    }

    private InstitutionAdmin findInstitutionAdminById(Long institutionId) {
        return institutionAdminRepository.findById(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
    }
}
