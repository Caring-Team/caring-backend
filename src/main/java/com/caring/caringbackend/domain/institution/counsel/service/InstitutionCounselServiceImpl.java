package com.caring.caringbackend.domain.institution.counsel.service;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCounselCreateRequestDto;
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
     *
     * Note: InstitutionCounselDetail(예약 가능 시간)은 미리 생성하지 않음
     * - 사용자가 예약 가능 날짜 조회 시 동적으로 생성 (Lazy Loading)
     * - 네이버 예약 시스템과 동일한 방식
     * - 스토리지 효율적, 사용하지 않는 날짜는 DB에 저장 안 됨
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

        // Detail은 생성하지 않음 (Lazy Loading 방식)
        log.info("상담 서비스 등록 완료: institutionId={}, counselId={}, title={}",
                 institutionId, counsel.getId(), counsel.getTitle());
    }

    @Override
    public List<InstitutionCounselResponseDto> getInstitutionCounsels(Long institutionId) {
        List<InstitutionCounsel> counsels = institutionCounselRepository.findByInstitutionId(institutionId);

        return counsels.stream()
                .map(InstitutionCounselResponseDto::from)
                .toList();
    }

    @Override
    public CounselStatus toggleInstitutionCounselStatus(Long adminId, Long institutionId, Long counselId) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validate(institutionId, admin);

        InstitutionCounsel counsel = findInstitutionCounselByID(counselId);
        return counsel.toggleStatus();
    }

    private InstitutionCounsel findInstitutionCounselByID(Long counselId) {
        return institutionCounselRepository.findById(counselId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUNSEL_NOT_FOUND));
    }

    /**
     * 예약 가능 시간 조회 (Lazy Loading)
     * Detail이 없으면 자동 생성
     *
     * @param counselId 상담 서비스 ID
     * @param date 조회할 날짜
     * @return 해당 날짜의 예약 가능 시간 Detail
     */
    @Transactional
    public InstitutionCounselDetail getOrCreateCounselDetail(Long counselId, LocalDate date) {
        return counselDetailRepository
                .findByCounselIdAndServiceDate(counselId, date)
                .orElseGet(() -> {
                    log.info("예약 가능 시간 Detail 자동 생성: counselId={}, date={}", counselId, date);

                    InstitutionCounsel counsel = institutionCounselRepository
                            .findById(counselId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.COUNSEL_NOT_FOUND));

                    // 초기 상태: 모든 시간대 예약 가능 (48비트 모두 1)
                    // 0시~23시 30분 단위 = 48슬롯
                    long allAvailable = 0xFFFFFFFFFFFFL; // 48비트 모두 1

                    InstitutionCounselDetail detail = InstitutionCounselDetail.create(
                            counsel, date, allAvailable
                    );

                    return counselDetailRepository.save(detail);
                });
    }

    /**
     * 특정 시간대 예약 처리
     * 비트마스크에서 해당 시간 비트를 0으로 설정
     */
    @Transactional
    public void reserveTimeSlot(Long counselId, LocalDate date, int slotIndex) {
        InstitutionCounselDetail detail = getOrCreateCounselDetail(counselId, date);

        // 해당 슬롯 비트를 0으로 설정 (예약됨)
        long currentBitmask = detail.getTimeSlotsBitmask();
        long updatedBitmask = currentBitmask & ~(1L << slotIndex);

        detail.updateTimeSlotsBitmask(updatedBitmask);

        log.info("시간대 예약 완료: counselId={}, date={}, slot={}", counselId, date, slotIndex);
    }

    private void validate(Long institutionId, InstitutionAdmin admin) {
        if(!admin.hasInstitution()) {
            throw new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION);
        }

        if(!admin.belongsToInstitution(institutionId)) {
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
}
