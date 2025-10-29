package com.caring.caringbackend.domain.institution.profile.service;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.request.InstitutionUpdateRequestDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionProfileResponseDto;
import com.caring.caringbackend.domain.institution.profile.entity.ApprovalStatus;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.PriceInfo;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.global.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final GeocodingService geocodingService;

    /**
     * 기관 등록
     * @param requestDto 기관 생성 요청 DTO
     */
    @Override
    @Transactional
    public void registerInstitution(InstitutionCreateRequestDto requestDto) {
        // Address 생성
        Address address = Address.builder()
                .city(requestDto.getCity())
                .street(requestDto.getStreet())
                .zipCode(requestDto.getZipCode())
                .build();

        // Geocoding API를 통한 위도/경도 변환
        GeoPoint location = geocodingService.convertAddressToGeoPoint(address);

        // PriceInfo 생성
        PriceInfo priceInfo = PriceInfo.builder()
                .monthlyBaseFee(requestDto.getMonthlyBaseFee())
                .admissionFee(requestDto.getAdmissionFee())
                .monthlyMealCost(requestDto.getMonthlyMealCost())
                .priceNotes(requestDto.getPriceNotes())
                .build();

        // Institution 생성
        Institution institution = Institution.createInstitution(
                requestDto.getName(),
                requestDto.getInstitutionType(),
                requestDto.getPhoneNumber(),
                address,
                location,
                requestDto.getBedCount(),
                requestDto.getIsAdmissionAvailable(),
                priceInfo,
                requestDto.getOpeningHours()
        );

        // Institution 저장
        institutionRepository.save(institution);
        log.info("기관 등록 완료: id={}, name={}", institution.getId(), institution.getName());

        // TODO: 전문 질환 목록(specializedConditionCodes) 처리
    }

    @Override
    @Transactional(readOnly = true)
    public void getInstitutions() {
        // TODO: 기관 목록 조회 구현
    }

    /**
     * 기관 정보 수정 (PATCH - 선택적 업데이트)
     *
     * @param institutionId 기관 ID
     * @param requestDto    기관 수정 요청 DTO
     */
    @Override
    @Transactional
    public void updateInstitution(Long institutionId, InstitutionUpdateRequestDto requestDto) {
        // 기관 조회
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND));

        Address updatedAddress = null;
        GeoPoint updatedLocation = null;
        PriceInfo updatedPriceInfo = null;

        // 주소 및 위치 업데이트 처리
        if (hasAddressUpdate(requestDto)) {
            updatedAddress = Address.builder()
                    .city(requestDto.getCity() != null ? requestDto.getCity() : institution.getAddress().getCity())
                    .street(requestDto.getStreet() != null ? requestDto.getStreet() : institution.getAddress().getStreet())
                    .zipCode(requestDto.getZipCode() != null ? requestDto.getZipCode() : institution.getAddress().getZipCode())
                    .build();

            // 위,경도 재계산
            updatedLocation = geocodingService.convertAddressToGeoPoint(updatedAddress);
        }

        // 가격 정보 업데이트 처리
        if (hasPriceInfoUpdate(requestDto)) {
            PriceInfo currentPrice = institution.getPriceInfo();
            updatedPriceInfo = PriceInfo.builder()
                    .monthlyBaseFee(requestDto.getMonthlyBaseFee() != null ? requestDto.getMonthlyBaseFee() :
                            (currentPrice != null ? currentPrice.getMonthlyBaseFee() : null))
                    .admissionFee(requestDto.getAdmissionFee() != null ? requestDto.getAdmissionFee() :
                            (currentPrice != null ? currentPrice.getAdmissionFee() : null))
                    .monthlyMealCost(requestDto.getMonthlyMealCost() != null ? requestDto.getMonthlyMealCost() :
                            (currentPrice != null ? currentPrice.getMonthlyMealCost() : null))
                    .priceNotes(requestDto.getPriceNotes() != null ? requestDto.getPriceNotes() :
                            (currentPrice != null ? currentPrice.getPriceNotes() : null))
                    .build();
        }

        // Institution 엔티티 업데이트
        institution.updateInstitution(
                requestDto.getName(),
                requestDto.getPhoneNumber(),
                updatedAddress,     // 주소
                updatedLocation,    // 위치
                requestDto.getBedCount(),
                requestDto.getIsAdmissionAvailable(),
                updatedPriceInfo,   // 가격
                requestDto.getOpeningHours()
        );

        institutionRepository.save(institution);
        
        // TODO: 전문 질환 목록(specializedConditionCodes) 처리
    }

    /**
     * 주소 관련 필드가 업데이트 요청에 포함되어 있는지 확인
     */
    private boolean hasAddressUpdate(InstitutionUpdateRequestDto requestDto) {
        return requestDto.getCity() != null ||
                requestDto.getStreet() != null ||
                requestDto.getZipCode() != null;
    }

    /**
     * 가격 정보 관련 필드가 업데이트 요청에 포함되어 있는지 확인
     */
    private boolean hasPriceInfoUpdate(InstitutionUpdateRequestDto requestDto) {
        return requestDto.getMonthlyBaseFee() != null ||
                requestDto.getAdmissionFee() != null ||
                requestDto.getMonthlyMealCost() != null ||
                requestDto.getPriceNotes() != null;
    }

    /**
     * 기관 승인 처리
     *
     * @param institutionId 기관 ID
     */
    @Override
    @Transactional
    public void approveInstitution(Long institutionId) {
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND));

        institution.approveInstitution();
        institutionRepository.save(institution);
    }

    /**
     * 입소 가능 여부 변경 (별도 API)
     *
     * @param institutionId        기관 ID
     * @param isAdmissionAvailable 입소 가능 여부
     */
    @Override
    @Transactional
    public void changeAdmissionAvailability(Long institutionId, Boolean isAdmissionAvailable) {
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND));

        institution.changeAdmissionAvailability(isAdmissionAvailable);
        institutionRepository.save(institution);
    }
}
