package com.caring.caringbackend.api.internal.user.dto.elderly.request;

import com.caring.caringbackend.domain.user.elderly.entity.ActivityLevel;
import com.caring.caringbackend.domain.user.elderly.entity.BloodType;
import com.caring.caringbackend.domain.user.elderly.entity.CognitiveLevel;
import com.caring.caringbackend.domain.user.elderly.entity.LongTermCareGrade;
import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 🔄 어르신 프로필 수정 요청 DTO
 * <p>
 * 어르신 프로필 수정 시 필요한 정보를 담는 요청 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElderlyProfileUpdateRequest {

    /**
     * 👤 이름 (필수)
     */
    @NotBlank(message = "이름은 필수입니다.")
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
    @Pattern(regexp = "^[0-9-]+$", message = "전화번호는 숫자와 하이픈만 입력 가능합니다")
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
     * <p>
     * 등급이 있으면 인지수준, 활동레벨은 불필요합니다.
     * 등급이 없으면(NONE) 인지수준, 활동레벨이 필수입니다.
     */
    private LongTermCareGrade longTermCareGrade;

    /**
     * 📝 특이사항
     */
    private String notes;

    /**
     * 🏠 주소 정보
     * <p>
     * 주소 입력 시 서버에서 Geocoding API를 통해 자동으로 위경도를 계산합니다.
     */
    @Valid
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

