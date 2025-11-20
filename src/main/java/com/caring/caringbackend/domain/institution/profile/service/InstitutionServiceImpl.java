package com.caring.caringbackend.domain.institution.profile.service;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.request.InstitutionSearchFilter;
import com.caring.caringbackend.api.institution.dto.request.InstitutionUpdateRequestDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionDetailResponseDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionProfileResponseDto;
import com.caring.caringbackend.domain.file.entity.File;
import com.caring.caringbackend.domain.file.service.FileService;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.entity.PriceInfo;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.domain.tag.entity.InstitutionTag;
import com.caring.caringbackend.domain.tag.entity.Tag;
import com.caring.caringbackend.domain.tag.repository.InstitutionTagRepository;
import com.caring.caringbackend.domain.tag.repository.TagRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.global.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.caring.caringbackend.domain.file.entity.ReferenceType.INSTITUTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final InstitutionAdminRepository institutionAdminRepository;
    private final GeocodingService geocodingService;
    private final List<InstitutionSearchStrategy> searchStrategies;
    private final FileService fileService;
    private final TagRepository tagRepository;
    private final InstitutionTagRepository institutionTagRepository;

    /**
     * 기관 등록
     *
     * @param adminId 관리자 ID
     * @param requestDto 기관 생성 요청 DTO
     * @param file 사업자 등록증 파일
     */
    @Override
    @Transactional
    public void registerInstitution(Long adminId, InstitutionCreateRequestDto requestDto, MultipartFile file) {
        // 관리자 조회
        InstitutionAdmin admin = findInstitutionAdminById(adminId);

        // 이미 기관이 등록되어 있는지 확인
        if (admin.getInstitution() != null) {
            throw new BusinessException(ErrorCode.INSTITUTION_ALREADY_REGISTERED);
        }

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

        // 사업자등록증 파일 업로드 (임시 저장 - referenceId는 null)
        File uploadedFile = fileService.uploadAndLinkBusinessLicense(file, null);


        // Institution 생성 및 저장
        Institution institution = Institution.createInstitution(
                requestDto.getName(),
                requestDto.getInstitutionType(),
                requestDto.getPhoneNumber(),
                address,
                location,
                requestDto.getBedCount(),
                requestDto.getIsAdmissionAvailable(),
                priceInfo,
                requestDto.getOpeningHours(),
                requestDto.getBusinessLicense(),
                uploadedFile.getFileUrl()  // 업로드한 파일 URL
        );

        Institution savedInstitution = institutionRepository.save(institution);
        admin.linkInstitution(savedInstitution);

        // 파일의 참조 정보 업데이트 (기관 ID와 연결)
        if (uploadedFile.getId() != null) {
            fileService.updateFileReference(uploadedFile.getId(), savedInstitution.getId(), INSTITUTION);
        }
        
        // 태그 연결 (InstitutionTag 생성)
        if (requestDto.getTagIds() != null && !requestDto.getTagIds().isEmpty()) {
            saveInstitutionTags(savedInstitution, requestDto.getTagIds());
        }
        
        log.info("기관 등록 완료: institutionId={}, adminId={}", savedInstitution.getId(), adminId);
    }

    /**
     * 기관 목록 조회 (페이징, 검색, 필터링)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<InstitutionProfileResponseDto> getInstitutions(Pageable pageable, InstitutionSearchFilter filter) {
        // 적용 가능한 검색 전략 선택 (우선순위 순)
        InstitutionSearchStrategy strategy = searchStrategies.stream()
                .filter(s -> s.isApplicable(filter))
                .min(Comparator.comparingInt(InstitutionSearchStrategy::getPriority))
                .orElseThrow(() -> new IllegalStateException("검색 전략을 찾을 수 없습니다."));

        // 검색 실행
        Page<Institution> institutionPage = strategy.search(filter, pageable);

        log.info("기관 목록 조회 완료: total={}, page={}, size={}",
                institutionPage.getTotalElements(),
                institutionPage.getNumber(),
                institutionPage.getSize());

        // Entity -> DTO 변환
        return institutionPage.map(InstitutionProfileResponseDto::from);
    }

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
     * @param adminId 관리자 ID
     * @param institutionId 기관 ID
     * @param requestDto    기관 수정 요청 DTO
     */
    @Override
    @Transactional
    public void updateInstitution(Long adminId, Long institutionId, InstitutionUpdateRequestDto requestDto) {
        Institution institution = findInstitutionById(institutionId);
        InstitutionAdmin admin = findInstitutionAdminById(adminId);

        // 권한 체크: 해당 기관의 OWNER만 수정 가능
        validateAdminAuthorization(admin, institution, true);

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
        
        // 태그 업데이트 (기존 태그 삭제 후 재생성)
        if (requestDto.getTagIds() != null) {
            institutionTagRepository.deleteByInstitutionId(institutionId);
            if (!requestDto.getTagIds().isEmpty()) {
                saveInstitutionTags(institution, requestDto.getTagIds());
            }
        }

        log.info("기관 정보 수정 완료: adminId={}, id={}, name={}", adminId, institution.getId(), institution.getName());

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
     * @param adminId 관리자 ID
     * @param institutionId        기관 ID
     * @param isAdmissionAvailable 입소 가능 여부
     */
    @Override
    @Transactional
    public void changeAdmissionAvailability(Long adminId, Long institutionId, Boolean isAdmissionAvailable) {
        Institution institution = findInstitutionById(institutionId);
        InstitutionAdmin admin = findInstitutionAdminById(adminId);

        // 권한 체크: 해당 기관의 OWNER 또는 STAFF 모두 가능
        validateAdminAuthorization(admin, institution, false);

        institution.changeAdmissionAvailability(isAdmissionAvailable);
        log.info("입소 가능 여부 변경: adminId={}, id={}, isAdmissionAvailable={}", adminId, institution.getId(), isAdmissionAvailable);
    }

    /**
     * 기관 삭제 (Soft Delete)
     * @param adminId 관리자 ID
     * @param institutionId 기관 ID
     */
    @Override
    @Transactional
    public void deleteInstitution(Long adminId, Long institutionId) {
        Institution institution = findInstitutionById(institutionId);
        InstitutionAdmin admin = findInstitutionAdminById(adminId);

        // 권한 체크: 해당 기관의 OWNER만 삭제 가능
        validateAdminAuthorization(admin, institution, true);

        institution.deleteInstitution();
        log.info("기관 삭제 완료: adminId={}, id={}, name={}", adminId, institution.getId(), institution.getName());
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

    /**
     * 기관 관리자 조회 내부 메서드
     *
     * @param adminId 관리자 ID
     * @return InstitutionAdmin 엔티티
     */
    private InstitutionAdmin findInstitutionAdminById(Long adminId) {
        return institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
    }

    /**
     * 관리자 권한 검증
     *
     * @param admin 관리자
     * @param institution 기관
     * @param ownerOnly true이면 OWNER만 허용, false이면 OWNER/STAFF 모두 허용
     */
    private void validateAdminAuthorization(InstitutionAdmin admin, Institution institution, boolean ownerOnly) {
        // 해당 기관의 관리자인지 확인
        if (admin.getInstitution() == null || !admin.getInstitution().getId().equals(institution.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_INSTITUTION_ACCESS);
        }

        // OWNER만 허용하는 경우
        if (ownerOnly && !admin.isOwner()) {
            throw new BusinessException(ErrorCode.OWNER_PERMISSION_REQUIRED);
        }
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
    
    /**
     * 기관 태그 저장 헬퍼 메서드
     *
     * @param institution 기관
     * @param tagIds 태그 ID 목록
     */
    private void saveInstitutionTags(Institution institution, List<Long> tagIds) {
        // 1. 태그 조회
        List<Tag> tags = tagRepository.findAllByIdIn(tagIds);
        
        // 2. 존재하지 않는 태그 ID 검증
        if (tags.size() != tagIds.size()) {
            throw new BusinessException(ErrorCode.TAG_NOT_FOUND);
        }
        
        // 3. InstitutionTag 생성 및 저장
        List<InstitutionTag> institutionTags = tags.stream()
                .map(tag -> InstitutionTag.builder()
                        .institution(institution)
                        .tag(tag)
                        .build())
                .toList();
        
        institutionTagRepository.saveAll(institutionTags);
        
        log.debug("기관 태그 저장 완료: institutionId={}, tagCount={}", institution.getId(), institutionTags.size());
    }
}
