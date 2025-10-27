package com.caring.caringbackend.api.dto.member.request;

import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.Gender;
import com.caring.caringbackend.global.model.GeoPoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 👤 회원 가입 요청 DTO
 * <p>
 * 회원 가입 시 필요한 정보를 담는 요청 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreateRequest {

    /**
     * 📧 이메일 (필수)
     */
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

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
     * 👤 성별 (필수)
     * <p>
     * MALE, FEMALE, NOT_KNOWN, NOT_APPLICABLE
     */
    @NotNull(message = "성별은 필수입니다.")
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

