package com.caring.caringbackend.api.user.dto.elderly.request;

import com.caring.caringbackend.domain.user.elderly.entity.ActivityLevel;
import com.caring.caringbackend.domain.user.elderly.entity.BloodType;
import com.caring.caringbackend.domain.user.elderly.entity.CognitiveLevel;
import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 👵 어르신 프로필 등록 요청 DTO
 * <p>
 * 어르신 프로필 등록 시 필요한 정보를 담는 요청 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElderlyProfileCreateRequest {

    /**
     * 👤 이름 (필수)
     */
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    /**
     * 👤 성별 (필수)
     */
    @NotNull(message = "성별은 필수입니다.")
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
     * 📝 특이사항
     */
    private String notes;

    /**
     * 🏠 주소 정보 (필수)
     * <p>
     * 주소 입력 시 서버에서 Geocoding API를 통해 자동으로 위경도를 계산합니다.
     */
    @Valid
    @NotNull(message = "주소는 필수입니다.")
    private AddressDto address;

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
}

