package com.caring.caringbackend.api.user.dto.member.request;

import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.caring.caringbackend.global.model.Gender;

import java.time.LocalDate;

/**
 * 🔄 회원 정보 수정 요청 DTO
 * <p>
 * 회원 정보 수정 시 필요한 정보를 담는 요청 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequest {

    /**
     * 👤 이름 (필수)
     */
    @NotBlank(message = "이름은 필수입니다.")
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
     * 🏠 주소 정보
     */
    @Valid
    private AddressDto address;

    /**
     * 📍 위치 정보 (위도/경도)
     */
    @Valid
    private GeoPointDto location;

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
     * 🏠 주소 DTO를 Address 엔티티로 변환
     */
    public Address toAddress() {
        if (address == null) {
            return null;
        }
        return new Address(
            address.city,
            address.street,
            address.zipCode
        );
    }

    /**
     * 📍 위치 DTO를 GeoPoint 엔티티로 변환
     */
    public GeoPoint toGeoPoint() {
        if (location == null) {
            return null;
        }
        return new GeoPoint(
            location.latitude,
            location.longitude
        );
    }
}

