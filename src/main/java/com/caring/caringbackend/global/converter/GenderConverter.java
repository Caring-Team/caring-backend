package com.caring.caringbackend.global.converter;

import com.caring.caringbackend.global.model.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Gender Enum을 DB의 정수 값으로 자동 변환하는 JPA Converter
 */
@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Gender gender) {
        if (gender == null) {
            return null;
        }
        return gender.getCode();
    }

    @Override
    public Gender convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }
        return Gender.findEnumByCode(code);
    }
}
