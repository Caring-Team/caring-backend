package com.caring.caringbackend.global.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    // 도시
    @NotBlank
    private String city;

    // 거리
    @NotBlank
    private String street;

    // 우편번호
    @NotBlank
    private String zipCode;

    @Builder
    public Address(String city, String street, String zipCode) {
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return city + " " + street;
    }
}
