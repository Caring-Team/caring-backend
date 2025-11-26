package com.caring.caringbackend.api.internal.user.dto.elderly.response;

import com.caring.caringbackend.domain.user.elderly.entity.ActivityLevel;
import com.caring.caringbackend.domain.user.elderly.entity.BloodType;
import com.caring.caringbackend.domain.user.elderly.entity.CognitiveLevel;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.elderly.entity.LongTermCareGrade;
import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.Gender;
import com.caring.caringbackend.global.model.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 👵 어르신 프로필 응답 DTO
 * <p>
 * 어르신 프로필 조회 시 사용하는 응답 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElderlyProfileResponse {

    /**
     * 🔢 어르신 프로필 ID
     */
    private Long id;

    /**
     * 🔢 회원 ID (어르신 프로필의 보호자 접근)
     */
    private Long memberId;

    /**
     * 👤 이름
     */
    private String name;

    /**
     * 👤 성별
     */
    private Gender gender;

    /**
     * 🎂 생년월일
     */
    private LocalDate birthDate;

    /**
     * 🩸 혈액형
     */
    private BloodType bloodType;

    /**
     * 📱 전화번호
     */
    private String phoneNumber;

    /**
     * 🏃 활동 수준
     */
    private ActivityLevel activityLevel;

    /**
     * 🧠 인지 수준
     */
    private CognitiveLevel cognitiveLevel;

    /**
     * 🏥 장기요양등급
     */
    private LongTermCareGrade longTermCareGrade;

    /**
     * 📝 특이사항
     */
    private String notes;

    /**
     * 🏠 주소
     */
    private AddressDto address;

    /**
     * 📍 위치
     */
    private GeoPointDto location;

    /**
     * 📅 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 🔄 수정일시
     */
    private LocalDateTime updatedAt;

    /**
     * 🏠 주소 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {
        private String zipCode;
        private String city;
        private String street;
    }

    /**
     * 📍 위치 정보 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoPointDto {
        private Double latitude;
        private Double longitude;
    }

    /**
     * 📤 ElderlyProfile 엔티티를 ElderlyProfileResponse로 변환
     */
    public static ElderlyProfileResponse from(ElderlyProfile profile) {
        return ElderlyProfileResponse.builder()
            .id(profile.getId())
            .memberId(profile.getMember().getId())
            .name(profile.getName())
            .gender(profile.getGender())
            .birthDate(profile.getBirthDate())
            .bloodType(profile.getBloodType())
            .phoneNumber(profile.getPhoneNumber())
            .activityLevel(profile.getActivityLevel())
            .cognitiveLevel(profile.getCognitiveLevel())
            .longTermCareGrade(profile.getLongTermCareGrade())
            .notes(profile.getNotes())
            .address(toAddressDto(profile.getAddress()))
            .location(toGeoPointDto(profile.getLocation()))
            .createdAt(profile.getCreatedAt())
            .updatedAt(profile.getUpdatedAt())
            .build();
    }

    private static AddressDto toAddressDto(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDto.builder()
            .zipCode(address.getZipCode())
            .city(address.getCity())
            .street(address.getStreet())
            .build();
    }

    private static GeoPointDto toGeoPointDto(GeoPoint location) {
        if (location == null) {
            return null;
        }
        return GeoPointDto.builder()
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .build();
    }
}

