package com.caring.caringbackend.domain.institution.counsel.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CounselTimeUnit {
    HALF(1),
    FULL(2);

    private final int space;
}
