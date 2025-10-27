package com.caring.caringbackend.api.dto.member.response;

import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.entity.MemberRole;
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
 * 👤 회원 정보 응답 DTO
 * <p>
 * 회원 조회 시 사용하는 기본 응답 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    /**
     * 🔢 회원 ID
     */
    private Long id;

    /**
     * 👥 역할
     */
    private MemberRole role;

    /**
     * 📧 이메일
     */
    private String email;

    /**
     * 👤 이름
     */
    private String name;

    /**
     * 📱 전화번호
     */
    private String phoneNumber;

    /**
     * 👤 성별
     */
    private Gender gender;

    /**
     * 🎂 생년월일
     */
    private LocalDate birthDate;

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
     * 📤 Member 엔티티를 MemberResponse로 변환
     */
    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
            .id(member.getId())
            .role(member.getRole())
            .email(member.getEmail())
            .name(member.getName())
            .phoneNumber(member.getPhoneNumber())
            .gender(member.getGender())
            .birthDate(member.getBirthDate())
            .address(toAddressDto(member.getAddress()))
            .location(toGeoPointDto(member.getLocation()))
            .createdAt(member.getCreatedAt())
            .updatedAt(member.getUpdatedAt())
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

