package com.caring.caringbackend.domain.institution.profile.service;

import com.caring.caringbackend.api.internal.institution.dto.request.CareGiverCreateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.request.CareGiverUpdateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.response.CareGiverResponseDto;
import com.caring.caringbackend.domain.file.entity.File;
import com.caring.caringbackend.domain.file.service.FileService;
import com.caring.caringbackend.domain.institution.profile.entity.CareGiver;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.CareGiverRepository;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CareGiverServiceImpl implements CareGiverService {

    private final CareGiverRepository careGiverRepository;
    private final InstitutionAdminRepository institutionAdminRepository;
    private final FileService fileService;

    /**
     * 요양보호사 등록
     * */
    @Override
    public void registerCareGiver(
            Long adminId, CareGiverCreateRequestDto requestDto, MultipartFile photo
    ) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(admin);
        Institution institution = admin.getInstitution();

        CareGiver careGiver = CareGiver.createCareGiver(
                institution,
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPhoneNumber(),
                requestDto.getGender(),
                requestDto.getBirthDate(),
                requestDto.getExperienceDetails(),
                null // photoUrl은 나중에 업데이트
        );
        institution.addCareGiver(careGiver);
        CareGiver savedCareGiver = careGiverRepository.save(careGiver);

        if (photo != null && !photo.isEmpty()) {
            File uploadedFile = fileService.uploadCareGiverPhoto(photo, savedCareGiver.getId());
            savedCareGiver.updatePhotoUrl(uploadedFile.getFileUrl());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareGiverResponseDto> getCareGiversByInstitution(Long adminId) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(admin);
        Institution institution = admin.getInstitution();
        List<CareGiver> careGivers = careGiverRepository.findByInstitutionIdOrderByCreatedAtDesc(institution.getId());

        return careGivers.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CareGiverResponseDto getCareGiverDetail(Long adminId, Long careGiverId) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(admin);
        Institution institution = admin.getInstitution();

        CareGiver careGiver = getCareGiver(institution.getId(), careGiverId);
        return convertToDto(careGiver);
    }



    @Override
    public void updateCareGiver(Long adminId, Long careGiverId,
                                CareGiverUpdateRequestDto requestDto) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(admin);
        Institution institution = admin.getInstitution();

        // 요양보호사 조회 (기관 소속 확인)
        CareGiver careGiver = getCareGiver(institution.getId(), careGiverId);

        // 정보 수정 (null이 아닌 값만)
        careGiver.updateCareGiver(
                requestDto.name(),
                requestDto.email(),
                requestDto.phoneNumber(),
                requestDto.gender(),
                requestDto.birthDate(),
                requestDto.experienceDetails()
        );
    }

    @Override
    public void updateCareGiverPhoto(Long adminId, Long careGiverId, MultipartFile photo) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(admin);
        Institution institution = admin.getInstitution();

        // 요양보호사 조회
        CareGiver careGiver = getCareGiver(institution.getId(), careGiverId);

        // 사진 업로드 및 URL 업데이트
        if (photo != null && !photo.isEmpty()) {
            File uploadedFile = fileService.uploadCareGiverPhoto(photo, careGiverId);
            careGiver.updatePhotoUrl(uploadedFile.getFileUrl());
            log.info("요양보호사 사진 업데이트 완료 - CareGiverId: {}", careGiverId);
        }
    }

    @Override
    public void deleteCareGiver(Long adminId, Long careGiverId) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(admin);
        Institution institution = admin.getInstitution();
        getCareGiver(institution.getId(), careGiverId).softDelete();
    }

    private CareGiver getCareGiver(Long institutionId, Long careGiverId) {
        return careGiverRepository
                .findByIdAndInstitutionId(careGiverId, institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CAREGIVER_NOT_FOUND));
    }

    /**
     * CareGiver 엔티티를 DTO로 변환 (PreSigned URL 포함)
     */
    private CareGiverResponseDto convertToDto(CareGiver careGiver) {
        String photoUrl = careGiver.getPhotoUrl();
        String presignedPhotoUrl = photoUrl != null ? fileService.generatePresignedUrl(photoUrl) : null;

        return new CareGiverResponseDto(
                careGiver.getId(),
                careGiver.getName(),
                careGiver.getEmail(),
                careGiver.getPhoneNumber(),
                careGiver.getGender(),
                careGiver.getBirthDate(),
                careGiver.getExperienceDetails(),
                presignedPhotoUrl
        );
    }

    private void validate(InstitutionAdmin admin) {
        if (!admin.hasInstitution()) {
            throw new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION);
        }
    }

    private InstitutionAdmin findInstitutionAdminById(Long institutionId) {
        return institutionAdminRepository.findByIdWithInstitution(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
    }
}
