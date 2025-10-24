package com.caring.caringbackend.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 성별 Enum (ISO-IEC-5218 표준)
 * 0 = Not known (미기입/알 수 없음)
 * 1 = Male (남성)
 * 2 = Female (여성)
 * 9 = Not applicable (적용 불가)
 */
@Getter
@RequiredArgsConstructor
public enum Gender {
    
    NOT_KNOWN(0, "미기입"),
    MALE(1, "남성"),
    FEMALE(2, "여성"),
    NOT_APPLICABLE(9, "적용불가");

    private final int code;
    private final String description;

    /**
     * 코드 값으로 Gender enum 찾기
     *
     * @param code ISO-IEC-5218 코드 값
     * @return 해당하는 Gender enum
     * @throws IllegalArgumentException 유효하지 않은 코드인 경우
     */
    public static Gender findEnumByCode(int code) {
        for (Gender gender : values()) {
            if (gender.code == code) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid gender code: " + code);
    }
}
