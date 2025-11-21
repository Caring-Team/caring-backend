package com.caring.caringbackend.domain.institution.advertisement.service;

import com.caring.caringbackend.api.institution.dto.request.advertisement.AdvertisementCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.response.advertisement.*;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 광고 관리 서비스 인터페이스
 */
public interface AdvertisementService {

    // ==================== 기관 광고 신청 ====================

    /**
     * 광고 신청 (Request 생성)
     */
    AdvertisementRequestResponseDto createAdvertisementRequest(Long institutionId, AdvertisementCreateRequestDto requestDto, Long adminId);

    /**
     * 내 기관 신청 목록 조회
     */
    List<AdvertisementSummaryDto> getInstitutionRequests(
            Long institutionId,
            AdvertisementStatus status,
            AdvertisementType type,
            Long adminId
    );

    /**
     * 신청 상세 조회
     */
    AdvertisementRequestDetailDto getRequestDetail(Long institutionId, Long requestId, Long adminId);

    /**
     * 신청 취소 (승인 대기 중에만 가능)
     */
    void cancelRequest(Long institutionId, Long requestId, Long adminId);

    // ==================== 기관 승인된 광고 조회 ====================

    /**
     * 내 기관의 승인된 광고 목록 조회
     */
    List<AdvertisementSummaryDto> getInstitutionAdvertisements(
            Long institutionId,
            AdvertisementStatus status,
            Long adminId
    );

    /**
     * 승인된 광고 상세 조회
     */
    AdvertisementResponseDto getAdvertisementDetail(Long institutionId, Long adId, Long adminId);

    /**
     * 승인된 광고 취소 (PENDING 상태만)
     */
    AdvertisementResponseDto cancelAdvertisement(Long institutionId, Long adId, String cancelReason, Long adminId);

    // ==================== 관리자 신청 심사 ====================

    /**
     * 전체 신청 목록 조회 (관리자용)
     */
    Page<AdvertisementSummaryDto> getAllRequests(
            AdvertisementStatus status,
            AdvertisementType type,
            Pageable pageable
    );

    /**
     * 승인 대기 신청 목록 조회
     */
    Page<AdvertisementSummaryDto> getPendingRequests(Pageable pageable);

    /**
     * 신청 상세 조회 (관리자용)
     */
    AdvertisementRequestDetailDto getRequestDetailForAdmin(Long requestId);

    /**
     * 신청 승인 (Request → Advertisement 생성)
     */
    AdvertisementResponseDto approveRequest(Long requestId, String memo);

    /**
     * 신청 거절
     */
    AdvertisementRequestDetailDto rejectRequest(Long requestId, String rejectionReason);

    // ==================== 관리자 광고 관리 ====================

    /**
     * 전체 광고 목록 조회
     */
    Page<AdvertisementSummaryDto> getAllAdvertisements(
            AdvertisementStatus status,
            AdvertisementType type,
            Pageable pageable
    );

    /**
     * 광고 상세 조회 (관리자용)
     */
    AdvertisementResponseDto getAdvertisementDetailForAdmin(Long adId);

    /**
     * 광고 강제 종료
     */
    AdvertisementResponseDto forceEndAdvertisement(Long advertisementId, String reason);

    // ==================== 공개 광고 조회 ====================

    /**
     * 현재 진행중인 광고 목록 조회 (공개 API)
     */
    List<ActiveAdvertisementDto> getActiveAdvertisements();

    /**
     * 유형별 진행중인 광고 조회 (공개 API)
     */
    List<ActiveAdvertisementDto> getActiveAdvertisementsByType(AdvertisementType type);
}

