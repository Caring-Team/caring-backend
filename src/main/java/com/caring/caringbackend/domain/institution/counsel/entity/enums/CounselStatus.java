package com.caring.caringbackend.domain.institution.counsel.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CounselStatus {
    ACTIVE("활성화 상태"),
    INACTIVE("비활성화 상태");

    private final String description;

}
