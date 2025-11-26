package com.caring.caringbackend.domain.institution.counsel.service;

import static com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselTimeUnit.FULL;
import static com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselTimeUnit.HALF;

import com.caring.caringbackend.api.internal.institution.dto.CounselHourDto;
import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionCounselCreateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionCounselUpdateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionCounselDetailResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionCounselReservationDetailResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionCounselResponseDto;
import com.caring.caringbackend.domain.institution.counsel.entity.CounselHours;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselReservationStatus;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselStatus;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselTimeUnit;
import com.caring.caringbackend.domain.institution.counsel.repository.CounselHoursRepository;
import com.caring.caringbackend.domain.institution.counsel.repository.InstitutionCounselDetailRepository;
import com.caring.caringbackend.domain.institution.counsel.repository.InstitutionCounselRepository;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final CounselHoursRepository counselHoursRepository;

    /**
     * 기관 상담 서비스 등록 InstitutionCounselDetail 생성하지 않음 - 사용자가 예약 가능 날짜 조회 시 동적으로 생성 (Lazy Loading)
     */
    @Override
    public void createInstitutionCounsel(Long adminId,
                                         InstitutionCounselCreateRequestDto requestDto) {
        validateReservableDays(requestDto.getMinReservableDaysBefore(), requestDto.getMaxReservableDaysBefore());
        validateCounselHour(requestDto.getUnit(), requestDto.getCounselHours());

        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validateInstitution(admin);
        Institution institution = admin.getInstitution();

        InstitutionCounsel counsel = InstitutionCounsel.createInstitutionCounsel(
                institution,
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.getMinReservableDaysBefore(),
                requestDto.getMaxReservableDaysBefore(),
                requestDto.getUnit()
        );

        institutionCounselRepository.save(counsel);
        makeCounselHours(counsel, requestDto.getCounselHours());
    }

    /**
     * 기관의 상담 서비스 목록 조회
     */
    @Override
    public List<InstitutionCounselResponseDto> getInstitutionCounsels(Long adminId) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validateInstitution(admin);
        Institution institution = admin.getInstitution();

        List<InstitutionCounsel> counsels = institutionCounselRepository.findByInstitutionId(institution.getId());

        return counsels.stream()
                .map(InstitutionCounselResponseDto::from)
                .toList();
    }

    /**
     * 기관 상담 서비스 활성화/비활성화 토글
     */
    @Override
    public CounselStatus toggleInstitutionCounselStatus(Long adminId, Long counselId) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validateInstitution(admin);
        Institution institution = admin.getInstitution();

        InstitutionCounsel counsel = findInstitutionCounselById(counselId);
        validateCounselOwnership(institution, counsel);

        return counsel.toggleStatus();
    }

    /**
     * 기관 상담 서비스 삭제 (논리 삭제)
     */
    @Override
    public void deleteCounselByCounselId(Long adminId, Long counselId) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validateInstitution(admin);
        Institution institution = admin.getInstitution();

        InstitutionCounsel counsel = findInstitutionCounselById(counselId);
        validateCounselOwnership(institution, counsel);
        counsel.delete();
    }

    @Override
    public InstitutionCounselDetailResponseDto getCounselDetail(Long adminId, Long counselId) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validateInstitution(admin);
        Institution institution = admin.getInstitution();

        InstitutionCounsel counsel = findInstitutionCounselById(counselId);
        validateCounselOwnership(institution, counsel);

        Set<CounselHours> counselHours = counselHoursRepository.findAllByCounselId(counselId);

        Set<CounselHourDto> counselHourDtos = toCounselHourDtos(counselHours);

        return InstitutionCounselDetailResponseDto.builder()
                .id(counsel.getId())
                .title(counsel.getTitle())
                .description(counsel.getDescription())
                .minReservableDaysBefore(counsel.getMinReservableDaysBefore())
                .maxReservableDaysBefore(counsel.getMaxReservableDaysBefore())
                .counselStatus(counsel.getStatus())
                .unit(counsel.getUnit())
                .counselHours(counselHourDtos)
                .build();

    }

    /**
     * 예약 가능 시간 조회 (Lazy Loading) Detail이 없으면 자동 생성
     *
     * @param counselId 상담 서비스 ID
     * @param date      조회할 날짜
     * @return 해당 날짜의 예약 가능 시간 Detail
     */
    @Override
    public InstitutionCounselReservationDetailResponseDto getOrCreateCounselDetail(Long counselId, LocalDate date) {
        InstitutionCounselDetail counselDetail = counselDetailRepository
                .findByCounselIdAndServiceDate(counselId, date)
                .orElseGet(() -> createNewCounselDetail(counselId, date));

        return InstitutionCounselReservationDetailResponseDto.from(counselDetail);
    }

    /**
     * 기관 상담 서비스 정보 수정
     */
    @Override
    public void updateInstitutionCounsel(Long adminId, Long counselId,
                                         InstitutionCounselUpdateRequestDto requestDto) {
        InstitutionAdmin admin = findInstitutionAdminById(adminId);
        validateInstitution(admin);

        Institution institution = admin.getInstitution();

        InstitutionCounsel counsel = findInstitutionCounselById(counselId);
        validateCounselOwnership(institution, counsel);

        counsel.updateInfo(requestDto.getTitle(), requestDto.getDescription(),
                requestDto.getMinReservableDaysBefore(), requestDto.getMaxReservableDaysBefore());

        if (requestDto.getCounselHours() != null && !requestDto.getCounselHours().isEmpty()) {
            validateCounselHour(counsel.getUnit(), requestDto.getCounselHours());
            counselHoursRepository.deleteAllByCounselId(counselId);
            makeCounselHours(counsel, requestDto.getCounselHours());
        }
    }

    // ==================== Private Methods ====================

    private void makeCounselHours(InstitutionCounsel counsel,
                                  Set<CounselHourDto> dto) {
        List<CounselHours> counsels = new ArrayList<>();

        for (var counselHour : dto) {
            for (var day : counselHour.getDays()) {
                CounselHours entity = CounselHours.builder()
                        .counsel(counsel)
                        .startTime(counselHour.getStartTime())
                        .endTime(counselHour.getEndTime())
                        .dayOfWeek(day)
                        .build();

                counsels.add(entity);
            }
        }

        counselHoursRepository.saveAll(counsels);
    }

    /**
     * 새로운 상담 예약 가능 시간 Detail 생성 초기 상태: 모든 시간대 예약 가능 (48비트 모두 1)
     *
     * @param counselId 상담 서비스 ID
     * @param date      서비스 날짜
     * @return 생성된 CounselDetail
     */
    private InstitutionCounselDetail createNewCounselDetail(Long counselId, LocalDate date) {
        log.info("예약 가능 시간 Detail 자동 생성: counselId={}, date={}", counselId, date);

        InstitutionCounsel counsel = findInstitutionCounselById(counselId);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        Set<CounselHours> counselHours = counselHoursRepository.findAllByCounselIdAndDayOfWeek(counselId, dayOfWeek);
        String allUnavailableTimeSlots = initAllUnavailableTimeSlots();
        CounselTimeUnit unit = counsel.getUnit();

        int space = unit.getSpace();
        char[] time = allUnavailableTimeSlots.toCharArray();
        for (var counselHour : counselHours) {
            int startSlot = timeToSlotIndex(counselHour.getStartTime());
            int endSlot = timeToSlotIndex(counselHour.getEndTime());

            for (int i = startSlot; i <= endSlot; i += space) {
                time[i] = CounselReservationStatus.AVAILABLE.getCode();
            }
        }

        InstitutionCounselDetail detail =
                InstitutionCounselDetail.create(counsel, date, new String(time));
        return counselDetailRepository.save(detail);
    }

    /**
     * 모든 시간대를 예약 불가능 상태로 초기화 48비트 모두 1로 설정 (0시~23시 30분 단위)
     *
     * @return 초기 비트마스크 값 (이진수 문자열 48개의 1)
     */
    private String initAllUnavailableTimeSlots() {
        return String.valueOf(CounselReservationStatus.UNAVAILABLE.getCode()).repeat(48);
    }


    private InstitutionCounsel findInstitutionCounselById(Long counselId) {
        return institutionCounselRepository.findById(counselId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUNSEL_NOT_FOUND));
    }

    private void validateInstitution(InstitutionAdmin admin) {
        if (!admin.hasInstitution()) {
            throw new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION);
        }
    }

    private Institution findInstitutionByAdminId(Long adminId) {
        return institutionRepository.findByAdminId(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION));
    }

    private InstitutionAdmin findInstitutionAdminById(Long adminId) {
        return institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
    }

    private static void validateCounselOwnership(Institution institution, InstitutionCounsel counsel) {
        if (!counsel.getInstitution().getId().equals(institution.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_INSTITUTION_ACCESS);
        }
    }

    private void validateReservableDays(Integer min, Integer max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("최소, 최대 예약일은 필수입니다.");
        }
        if (max < min) {
            throw new IllegalArgumentException("최대 예약일은 최소 예약일보다 작을 수 없습니다.");
        }
    }

    private void validateCounselHour(CounselTimeUnit unit, Set<CounselHourDto> dto) {
        Map<DayOfWeek, boolean[]> map = new HashMap<>();

        for (var day : DayOfWeek.values()) {
            map.put(day, new boolean[48]);
        }

        for (var counselHour : dto) {
            validateTimeUnit(unit, counselHour.getStartTime());
            validateTimeUnit(unit, counselHour.getEndTime());

            int startSlot = timeToSlotIndex(counselHour.getStartTime());
            int endTime = timeToSlotIndex(counselHour.getEndTime());

            for (var day : counselHour.getDays()) {
                boolean[] booleans = map.get(day);
                for (int i = startSlot; i <= endTime; i++) {
                    if (booleans[i]) {
                        throw new IllegalArgumentException("예약 가능 시간은 중복될 수 없습니다.");
                    }
                    booleans[i] = true;
                }
            }
        }
    }

    private void validateTimeUnit(CounselTimeUnit unit, LocalTime time) {
        if (unit == HALF) {
            if (time.isAfter(LocalTime.of(23, 30))) {
                throw new IllegalArgumentException("설정 가능한 마지막 시간은 23:30 입니다.");
            }
            if (time.getMinute() % 30 != 0) {
                throw new IllegalArgumentException("시간은 30분 단위로 입력해야 합니다.");
            }
        } else if (unit == FULL) {
            if (time.isAfter(LocalTime.of(23, 0))) {
                throw new IllegalArgumentException("설정 가능한 마지막 시간은 23:00 입니다.");
            }
            if (time.getMinute() != 0) {
                throw new IllegalArgumentException("시간은 1시간 단위로 입력해야 합니다.");
            }
        } else {
            throw new IllegalArgumentException("잘못된 시간 단위입니다.");
        }
    }


    private Set<CounselHourDto> toCounselHourDtos(Set<CounselHours> hoursList) {
        return hoursList.stream()
                // startTime + endTime 기준으로 그룹핑
                .collect(Collectors.groupingBy(
                        h -> new AbstractMap.SimpleEntry<>(h.getStartTime(), h.getEndTime())
                ))
                .entrySet().stream()
                .map(entry -> {
                    LocalTime start = entry.getKey().getKey();
                    LocalTime end = entry.getKey().getValue();
                    Set<DayOfWeek> days = entry.getValue().stream()
                            .map(CounselHours::getDayOfWeek)
                            .collect(Collectors.toSet());
                    return new CounselHourDto(days, start, end);
                }).collect(Collectors.toSet());
    }

    // returns 0 to 47
    private int timeToSlotIndex(LocalTime time) {
        int hour = time.getHour();
        int minute = time.getMinute();
        return hour * 2 + minute / 30;
    }
}
