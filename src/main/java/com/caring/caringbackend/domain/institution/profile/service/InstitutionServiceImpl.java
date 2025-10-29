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

    @Override
    @Transactional
    public void updateInstitution(InstitutionUpdateRequestDto requestDto) {
        // TODO: 기관 정보 수정 구현
    }

    @Override
    @Transactional
    public void approveInstitution(Long institutionId) {
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND));

        institution.approveInstitution();
        institutionRepository.save(institution);
    }
}
