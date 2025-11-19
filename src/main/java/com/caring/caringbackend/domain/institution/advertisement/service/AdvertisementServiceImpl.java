package com.caring.caringbackend.domain.institution.advertisement.service;

import com.caring.caringbackend.api.institution.dto.request.advertisement.AdvertisementCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.response.advertisement.*;
import com.caring.caringbackend.domain.institution.advertisement.entity.*;
import com.caring.caringbackend.domain.institution.advertisement.repository.InstitutionAdvertisementRepository;
import com.caring.caringbackend.domain.institution.advertisement.repository.InstitutionAdvertisementRequestRepository;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 광고 관리 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertisementServiceImpl implements AdvertisementService {

    private final InstitutionAdvertisementRequestRepository requestRepository;
    private final InstitutionAdvertisementRepository advertisementRepository;
    private final InstitutionRepository institutionRepository;
    private final InstitutionAdminRepository institutionAdminRepository;

    // ==================== 기관 광고 신청 ====================

    @Override
    @Transactional
    public AdvertisementRequestResponseDto createAdvertisementRequest(
            Long institutionId,
            AdvertisementCreateRequestDto requestDto,
            Long adminId
    ) {
        requestDto.validate();
        validateOwnerPermission(adminId, institutionId);

        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND));

        InstitutionAdvertisementRequest request = InstitutionAdvertisementRequest.create(
                institution,
                requestDto.type(),
                requestDto.title(),
                requestDto.description(),
                requestDto.bannerImageUrl(),
                requestDto.startDateTime(),
                requestDto.endDateTime()
        );

        InstitutionAdvertisementRequest savedRequest = requestRepository.save(request);
        return AdvertisementRequestResponseDto.from(savedRequest);
    }

    @Override
    public List<AdvertisementSummaryDto> getInstitutionRequests(
            Long institutionId,
            AdvertisementStatus status,
            AdvertisementType type,
            Long adminId
    ) {
        validateInstitutionAccess(adminId, institutionId);
        List<InstitutionAdvertisementRequest> institutionAdvertisementRequests = requestRepository.findByInstitutionIdWithFilters(institutionId, status, type);

        return institutionAdvertisementRequests.stream()
                .map(AdvertisementSummaryDto::fromRequest)
                .toList();
    }

    @Override
    public AdvertisementRequestDetailDto getRequestDetail(Long institutionId, Long requestId, Long adminId) {
        validateInstitutionAccess(adminId, institutionId);

        InstitutionAdvertisementRequest request = requestRepository.findByIdAndInstitutionId(requestId, institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        return AdvertisementRequestDetailDto.from(request);
    }

    @Override
    @Transactional
    public void cancelRequest(Long institutionId, Long requestId, Long adminId) {
        validateOwnerPermission(adminId, institutionId);

        InstitutionAdvertisementRequest request = requestRepository.findByIdAndInstitutionId(requestId, institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        if (!request.isPending()) {
            throw new BusinessException(ErrorCode.INVALID_ADVERTISEMENT_STATUS);
        }

        requestRepository.delete(request);
    }

    // ==================== 기관 승인된 광고 조회 ====================

    @Override
    public List<AdvertisementSummaryDto> getInstitutionAdvertisements(
            Long institutionId,
            AdvertisementStatus status,
            Long adminId
    ) {
        validateInstitutionAccess(adminId, institutionId);
        List<InstitutionAdvertisement> advertisements;

        if (status != null) {
            advertisements = advertisementRepository.findByInstitutionIdAndStatus(institutionId, status);
        } else {
            advertisements = advertisementRepository.findByInstitutionId(institutionId);
        }

        return advertisements.stream()
                .map(AdvertisementSummaryDto::fromAdvertisement)
                .toList();
    }

    @Override
    public AdvertisementResponseDto getAdvertisementDetail(Long institutionId, Long adId, Long adminId) {
        log.info("광고 상세 조회 - institutionId: {}, adId: {}", institutionId, adId);
        validateInstitutionAccess(adminId, institutionId);

        InstitutionAdvertisement advertisement = advertisementRepository.findByIdAndInstitutionId(adId, institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        return AdvertisementResponseDto.from(advertisement);
    }

    @Override
    @Transactional
    public AdvertisementResponseDto cancelAdvertisement(
            Long institutionId,
            Long adId,
            String cancelReason,
            Long adminId
    ) {
        validateOwnerPermission(adminId, institutionId);

        InstitutionAdvertisement advertisement = advertisementRepository.findByIdAndInstitutionId(adId, institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        advertisement.cancel(cancelReason);
        return AdvertisementResponseDto.from(advertisement);
    }

    // ==================== 관리자 신청 심사 ====================

    @Override
    public Page<AdvertisementSummaryDto> getAllRequests(
            AdvertisementStatus status,
            AdvertisementType type,
            Pageable pageable
    ) {
        log.info("전체 신청 목록 조회 (관리자) - status: {}, type: {}", status, type);
        Page<InstitutionAdvertisementRequest> requestPage =
            requestRepository.findAllWithFilters(status, type, pageable);

        return requestPage.map(AdvertisementSummaryDto::fromRequest);
    }

    @Override
    public Page<AdvertisementSummaryDto> getPendingRequests(Pageable pageable) {
        log.info("승인 대기 신청 목록 조회");

        Page<InstitutionAdvertisementRequest> requestPage = requestRepository.findByStatus(
                AdvertisementStatus.REQUEST_PENDING,
                pageable
        );

        return requestPage.map(AdvertisementSummaryDto::fromRequest);
    }

    @Override
    public AdvertisementRequestDetailDto getRequestDetailForAdmin(Long requestId) {
        log.info("신청 상세 조회 (관리자) - requestId: {}", requestId);

        InstitutionAdvertisementRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        return AdvertisementRequestDetailDto.from(request);
    }

    @Override
    @Transactional
    public AdvertisementResponseDto approveRequest(Long requestId, String memo) {
        log.info("신청 승인 - requestId: {}", requestId);

        InstitutionAdvertisementRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        // 신청 승인 처리
        request.approve(memo);

        // 새로운 광고 엔티티 생성
        InstitutionAdvertisement advertisement = request.toAdvertisement();
        InstitutionAdvertisement savedAdvertisement = advertisementRepository.save(advertisement);

        log.info("신청 승인 완료 - requestId: {}, 생성된 광고 ID: {}, 상태: {}",
                requestId, savedAdvertisement.getId(), savedAdvertisement.getStatus());

        return AdvertisementResponseDto.from(savedAdvertisement);
    }

    @Override
    @Transactional
    public AdvertisementRequestDetailDto rejectRequest(Long requestId, String rejectionReason) {
        log.info("신청 거절 - requestId: {}", requestId);

        InstitutionAdvertisementRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        request.reject(rejectionReason);
        log.info("신청 거절 완료 - requestId: {}", requestId);

        return AdvertisementRequestDetailDto.from(request);
    }

    // ==================== 관리자 광고 관리 ====================

    @Override
    public Page<AdvertisementSummaryDto> getAllAdvertisements(
            AdvertisementStatus status,
            AdvertisementType type,
            Pageable pageable
    ) {
        log.info("전체 광고 목록 조회 (관리자) - status: {}, type: {}", status, type);

        Page<InstitutionAdvertisement> advertisementPage = advertisementRepository.findAllWithFilters(status, type, pageable);
        return advertisementPage.map(AdvertisementSummaryDto::fromAdvertisement);
    }

    @Override
    public AdvertisementResponseDto getAdvertisementDetailForAdmin(Long adId) {
        log.info("광고 상세 조회 (관리자) - adId: {}", adId);

        InstitutionAdvertisement advertisement = advertisementRepository.findById(adId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        return AdvertisementResponseDto.from(advertisement);
    }

    @Override
    @Transactional
    public AdvertisementResponseDto forceEndAdvertisement(Long advertisementId, String reason) {
        log.info("광고 강제 종료 - adId: {}", advertisementId);

        InstitutionAdvertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        advertisement.forceEnd(reason);
        log.info("광고 강제 종료 완료 - adId: {}", advertisementId);
        return AdvertisementResponseDto.from(advertisement);
    }

    // ==================== 공개 광고 조회 ====================

    @Override
    public List<ActiveAdvertisementDto> getActiveAdvertisements() {
        log.info("현재 진행중인 광고 목록 조회");

        List<InstitutionAdvertisement> activeAds = advertisementRepository.findActiveAdvertisements(
                AdvertisementStatus.ADVERTISEMENT_ACTIVE,
                LocalDateTime.now()
        );

        return activeAds.stream()
                .map(ActiveAdvertisementDto::from)
                .toList();
    }

    @Override
    public List<ActiveAdvertisementDto> getActiveAdvertisementsByType(AdvertisementType type) {
        log.info("유형별 진행중인 광고 조회 - type: {}", type);

        List<InstitutionAdvertisement> activeAds = advertisementRepository.findActiveAdvertisementsByType(
                AdvertisementStatus.ADVERTISEMENT_ACTIVE,
                type,
                LocalDateTime.now()
        );

        return activeAds.stream()
                .map(ActiveAdvertisementDto::from)
                .toList();
    }

    // ==================== 권한 검증 ====================

    private void validateOwnerPermission(Long adminId, Long institutionId) {
        InstitutionAdmin admin = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        if (!admin.getInstitution().getId().equals(institutionId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_INSTITUTION_ACCESS);
        }

        if (!admin.isOwner()) {
            throw new BusinessException(ErrorCode.OWNER_PERMISSION_REQUIRED);
        }
    }

    private void validateInstitutionAccess(Long adminId, Long institutionId) {
        InstitutionAdmin admin = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        if (!admin.getInstitution().getId().equals(institutionId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_INSTITUTION_ACCESS);
        }
    }
}

