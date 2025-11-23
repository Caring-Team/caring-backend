package com.caring.caringbackend.domain.institution.counsel.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 상담 요청 상태
 */
@Getter
@RequiredArgsConstructor
public enum ConsultRequestStatus {
    
    PENDING("대기 중", "상담 요청이 접수되어 기관의 응답을 기다리는 상태"),
    ACCEPTED("수락됨", "기관이 상담 요청을 수락한 상태"),
    IN_PROGRESS("진행 중", "상담이 진행 중인 상태"),
    COMPLETED("완료됨", "상담이 완료된 상태"),
    CANCELLED("취소됨", "회원이 상담 요청을 취소한 상태"),
    REJECTED("거절됨", "기관이 상담 요청을 거절한 상태");
    
    private final String description;
    private final String detail;
}

