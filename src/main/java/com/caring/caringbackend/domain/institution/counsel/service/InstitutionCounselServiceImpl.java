package com.caring.caringbackend.domain.institution.counsel.service;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCounselCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.request.InstitutionCounselUpdateRequestDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionCounselDetailResponseDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionCounselResponseDto;
import com.caring.caringbackend.domain.institution.counsel.entity.CounselStatus;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;
import com.caring.caringbackend.domain.institution.counsel.repository.InstitutionCounselDetailRepository;
import com.caring.caringbackend.domain.institution.counsel.repository.InstitutionCounselRepository;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InstitutionCounselServiceImpl implements InstitutionCounselService {

    private final InstitutionCounselRepository institutionCounselRepository;
    private final InstitutionCounselDetailRepository counselDetailRepository;
    private final InstitutionRepository institutionRepository;
    private final InstitutionAdminRepository institutionAdminRepository;

    /**
     * 기관 상담 서비스 등록
     * InstitutionCounselDetail 생성하지 않음
     * - 사용자가 예약 가능 날짜 조회 시 동적으로 생성 (Lazy Loading)
     */
    @Override
    public void createInstitutionCounsel(Long adminId, Long institutionId, InstitutionCounselCreateRequestDto requestDto) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(institutionId, admin);

        Institution institution = findInstitutionById(institutionId);



        InstitutionCounsel counsel = InstitutionCounsel.createInstitutionCounsel(
                institution,
                requestDto.getTitle(),
                requestDto.getDescription()
        );

        institutionCounselRepository.save(counsel);
    }

    /**
     * 기관의 상담 서비스 목록 조회
     */
    @Override
    public List<InstitutionCounselResponseDto> getInstitutionCounsels(Long institutionId) {
        List<InstitutionCounsel> counsels = institutionCounselRepository.findByInstitutionId(institutionId);

        return counsels.stream()
                .map(InstitutionCounselResponseDto::from)
                .toList();
    }

    /**
     * 기관 상담 서비스 활성화/비활성화 토글
     */
    @Override
    public CounselStatus toggleInstitutionCounselStatus(Long adminId, Long institutionId, Long counselId) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(institutionId, admin);

        InstitutionCounsel counsel = findInstitutionCounselById(counselId);
        validateCounselOwnership(institutionId, counsel);

        return counsel.toggleStatus();
    }

    /**
     * 기관 상담 서비스 삭제 (논리 삭제)
     */
    @Override
    public void deleteCounselByCouncelId(Long adminId, Long institutionId, Long counselId) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(institutionId, admin);

        InstitutionCounsel counsel = findInstitutionCounselById(counselId);
        validateCounselOwnership(institutionId, counsel);
        counsel.delete();
    }

    /**
     * 예약 가능 시간 조회 (Lazy Loading)
     * Detail이 없으면 자동 생성
     *
     * @param counselId 상담 서비스 ID
     * @param date      조회할 날짜
     * @return 해당 날짜의 예약 가능 시간 Detail
     */
    @Override
    public InstitutionCounselDetailResponseDto getOrCreateCounselDetail(Long counselId, LocalDate date) {
        InstitutionCounselDetail counselDetail = counselDetailRepository
                .findByCounselIdAndServiceDate(counselId, date)
                .orElseGet(() -> createNewCounselDetail(counselId, date));

        return InstitutionCounselDetailResponseDto.from(counselDetail);
    }

    /**
     * 기관 상담 서비스 정보 수정
     */
    @Override
    public void updateInstitutionCounsel(Long adminId, Long institutionId, Long counselId,
                                         InstitutionCounselUpdateRequestDto requestDto) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(institutionId, admin);

        InstitutionCounsel counsel = findInstitutionCounselById(counselId);
        validateCounselOwnership(institutionId, counsel);
        counsel.updateInfo(requestDto.getTitle(), requestDto.getDescription());
    }

    // ==================== Private Methods ====================

    /**
     * 새로운 상담 예약 가능 시간 Detail 생성
     * 초기 상태: 모든 시간대 예약 가능 (48비트 모두 1)
     *
     * @param counselId 상담 서비스 ID
     * @param date      서비스 날짜
     * @return 생성된 CounselDetail
     */
    private InstitutionCounselDetail createNewCounselDetail(Long counselId, LocalDate date) {
        log.info("예약 가능 시간 Detail 자동 생성: counselId={}, date={}", counselId, date);

        InstitutionCounsel counsel = findInstitutionCounselById(counselId);
        // 0시~23시 30분 단위 = 48슬롯, 모든 시간대 예약 가능 (16진수 문자열)
        String allAvailable = initializeAllAvailableTimeSlots();
        InstitutionCounselDetail detail = InstitutionCounselDetail.create(
                counsel, date, allAvailable);

        return counselDetailRepository.save(detail);
    }

    /**
     * 모든 시간대를 예약 가능 상태로 초기화
     * 48비트 모두 1로 설정 (0시~23시 30분 단위)
     *
     * @return 초기 비트마스크 값 (이진수 문자열 48개의 1)
     */
    private String initializeAllAvailableTimeSlots() {
        return "1".repeat(48);
    }

    private InstitutionCounsel findInstitutionCounselById(Long counselId) {
        return institutionCounselRepository.findById(counselId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUNSEL_NOT_FOUND));
    }

    private void validate(Long institutionId, InstitutionAdmin admin) {
        if (!admin.hasInstitution()) {
            throw new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION);
        }

        if (!admin.belongsToInstitution(institutionId)) {
            throw new BusinessException(ErrorCode.ADMIN_INSTITUTION_MISMATCH);
        }
    }

    private Institution findInstitutionById(Long institutionId) {
        return institutionRepository.findById(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND));
    }

    private InstitutionAdmin findInstitutionAdminById(Long institutionId) {
        return institutionAdminRepository.findById(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND));
    }

    private static void validateCounselOwnership(Long institutionId, InstitutionCounsel counsel) {
        if (!counsel.getInstitution().getId().equals(institutionId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_INSTITUTION_ACCESS);
        }
    }
}
