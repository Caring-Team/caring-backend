package com.caring.caringbackend.domain.institution.profile.service;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.request.InstitutionUpdateRequestDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionDetailResponseDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionProfileResponseDto;
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

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final GeocodingService geocodingService;

    /**
     * 기관 등록
     *
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
    /**
     * 기관 상세 조회
     *
     * @param institutionId 기관 ID
     */
    @Override
    @Transactional(readOnly = true)
    public InstitutionDetailResponseDto getInstitutionDetail(Long institutionId) {
        // 기관 조회
        Institution institution = findInstitutionById(institutionId);
        // 활성 상태 검사
        validateIsActive(institution);
        // 승인 여부 검사
        validateIsApproved(institution);

        return InstitutionDetailResponseDto.entityToDto(institution);
    }

    /**
     * 기관 정보 수정 (PATCH)
     *
     * @param institutionId 기관 ID
     * @param requestDto    기관 수정 요청 DTO
     */
    @Override
    @Transactional
    public void updateInstitution(Long institutionId, InstitutionUpdateRequestDto requestDto) {
        Institution institution = findInstitutionById(institutionId);

        Address updatedAddress = buildUpdatedAddress(requestDto, institution);
        GeoPoint updatedLocation = calculateUpdatedLocation(requestDto, updatedAddress);
        PriceInfo updatedPriceInfo = buildUpdatedPriceInfo(requestDto, institution);

        institution.updateInstitution(
                requestDto.getName(),
                requestDto.getPhoneNumber(),
                updatedAddress,
                updatedLocation,
                requestDto.getBedCount(),
                requestDto.getIsAdmissionAvailable(),
                updatedPriceInfo,
                requestDto.getOpeningHours()
        );

        log.info("기관 정보 수정 완료: id={}, name={}", institution.getId(), institution.getName());

        // TODO: 전문 질환 목록(specializedConditionCodes) 처리
    }

    /**
     * 기관 승인 처리
     *
     * @param institutionId 기관 ID
     */
    @Override
    @Transactional
    public void approveInstitution(Long institutionId) {
        Institution institution = findInstitutionById(institutionId);
        institution.approveInstitution();
        log.info("기관 승인 완료: id={}, name={}", institution.getId(), institution.getName());
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
        Institution institution = findInstitutionById(institutionId);
        institution.changeAdmissionAvailability(isAdmissionAvailable);
        log.info("입소 가능 여부 변경: id={}, isAdmissionAvailable={}", institution.getId(), isAdmissionAvailable);
    }

    /**
     * 기관 삭제 (Soft Delete)
     * @param institutionId 기관 ID
     */
    @Override
    @Transactional
    public void deleteInstitution(Long institutionId) {
        Institution institution = findInstitutionById(institutionId);
        institution.deleteInstitution();
        log.info("기관 삭제 완료: id={}, name={}", institution.getId(), institution.getName());
    }


    /**
     * 기관 조회 내부 메서드
     *
     * @param institutionId 기관 ID
     * @return Institution 엔티티
     */
    private Institution findInstitutionById(Long institutionId) {
        return institutionRepository.findById(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND));
    }

    private static void validateIsActive(Institution institution) {
        if(!institution.isActive()) {
            throw new BusinessException(ErrorCode.INSTITUTION_INACTIVE);
        }
    }

    private static void validateIsApproved(Institution institution) {
        if(!institution.isApproved()) {
            throw new BusinessException(ErrorCode.INSTITUTION_APPROVAL_PENDING);
        }
    }

    /**
     * 업데이트된 Address 객체 생성
     * 주소 관련 필드가 있으면 기존 값과 병합하여 새 Address 반환
     *
     * @param requestDto  수정 요청 DTO
     * @param institution 기존 기관 엔티티
     * @return 업데이트된 Address (변경 없으면 null)
     */
    private Address buildUpdatedAddress(InstitutionUpdateRequestDto requestDto, Institution institution) {
        if (!hasAddressUpdate(requestDto)) {
            return null;
        }

        Address currentAddress = institution.getAddress();
        return Address.builder()
                .city(requestDto.getCity() != null ? requestDto.getCity() : currentAddress.getCity())
                .street(requestDto.getStreet() != null ? requestDto.getStreet() : currentAddress.getStreet())
                .zipCode(requestDto.getZipCode() != null ? requestDto.getZipCode() : currentAddress.getZipCode())
                .build();
    }

    /**
     * 업데이트된 위치(위도/경도) 계산
     * 주소가 변경된 경우 Geocoding API 호출
     *
     * @param requestDto     수정 요청 DTO
     * @param updatedAddress 업데이트된 Address
     * @return 업데이트된 GeoPoint (변경 없으면 null)
     */
    private GeoPoint calculateUpdatedLocation(InstitutionUpdateRequestDto requestDto, Address updatedAddress) {
        if (!hasAddressUpdate(requestDto) || updatedAddress == null) {
            return null;
        }

        GeoPoint location = geocodingService.convertAddressToGeoPoint(updatedAddress);
        log.info("주소 변경으로 인한 위치 업데이트: {}", location);
        return location;
    }

    /**
     * 업데이트된 PriceInfo 객체 생성
     * 가격 관련 필드가 있으면 기존 값과 병합하여 새 PriceInfo 반환
     *
     * @param requestDto  수정 요청 DTO
     * @param institution 기존 기관 엔티티
     * @return 업데이트된 PriceInfo (변경 없으면 null)
     */
    private PriceInfo buildUpdatedPriceInfo(InstitutionUpdateRequestDto requestDto, Institution institution) {
        if (!hasPriceInfoUpdate(requestDto)) {
            return null;
        }

        PriceInfo currentPrice = institution.getPriceInfo();
        return PriceInfo.builder()
                .monthlyBaseFee(getValueOrDefault(requestDto.getMonthlyBaseFee(), currentPrice, PriceInfo::getMonthlyBaseFee))
                .admissionFee(getValueOrDefault(requestDto.getAdmissionFee(), currentPrice, PriceInfo::getAdmissionFee))
                .monthlyMealCost(getValueOrDefault(requestDto.getMonthlyMealCost(), currentPrice, PriceInfo::getMonthlyMealCost))
                .priceNotes(getValueOrDefault(requestDto.getPriceNotes(), currentPrice, PriceInfo::getPriceNotes))
                .build();
    }

    /**
     * 새 값이 있으면 새 값 반환, 없으면 기존 값 반환
     *
     * @param newValue     새로운 값
     * @param currentPrice 현재 가격 정보
     * @param getter       getter 함수
     * @return 최종 값
     */
    private <T> T getValueOrDefault(T newValue, PriceInfo currentPrice,
                                    Function<PriceInfo, T> getter) {
        if (newValue != null) {
            return newValue;
        }
        return currentPrice != null ? getter.apply(currentPrice) : null;
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
}
