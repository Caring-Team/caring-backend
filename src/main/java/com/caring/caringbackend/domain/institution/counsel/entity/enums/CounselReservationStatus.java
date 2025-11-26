package com.caring.caringbackend.domain.institution.counsel.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CounselReservationStatus {

    RESERVED('0'),
    AVAILABLE('1'),
    UNAVAILABLE('2');

    private final char code;

}
