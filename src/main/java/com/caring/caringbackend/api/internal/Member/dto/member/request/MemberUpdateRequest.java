package com.caring.caringbackend.api.internal.Member.dto.member.request;

import com.caring.caringbackend.domain.institution.profile.entity.InstitutionType;
import com.caring.caringbackend.global.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.caring.caringbackend.global.model.Gender;

import java.time.LocalDate;
import java.util.List;

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
    @Pattern(regexp = "^[0-9-]+$", message = "전화번호는 숫자와 하이픈만 입력 가능합니다")
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
     * <p>
     * 주소 입력 시 서버에서 Geocoding API를 통해 자동으로 위경도를 계산합니다.
     */
    @Valid
    private AddressDto address;

    /**
     * 🏥 선호 기관 유형 (필수, 최소 1개 최대 3개)
     * <p>
     * 데이케어센터, 요양원, 재가 돌봄 서비스 중 선택 가능
     */
    @NotNull(message = "선호 기관 유형은 필수입니다.")
    @Size(min = 1, max = 3, message = "선호 기관 유형은 최소 1개, 최대 3개까지 선택할 수 있습니다.")
    private List<InstitutionType> preferredInstitutionTypes;

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

