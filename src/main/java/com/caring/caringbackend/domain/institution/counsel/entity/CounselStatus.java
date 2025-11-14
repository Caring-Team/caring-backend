package com.caring.caringbackend.domain.institution.counsel.entity;

import lombok.Getter;

@Getter
public enum CounselStatus {
    ACTIVE("활성화 상태"),
    INACTIVE("비활성화 상태");

    private final String description;

    CounselStatus(String description) {
        this.description = description;
    }
}
