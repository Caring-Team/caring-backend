package com.caring.caringbackend.domain.user.elderly.entity;

/**
 * 장기요양등급 (2024년 기준)
 * 등급이 없는 어르신은 NONE 으로 설정합니다.
 */
public enum LongTermCareGrade {
    GRADE_1,     // 1등급 (95점 이상)
    GRADE_2,     // 2등급 (75~95점 미만)
    GRADE_3,     // 3등급 (60~75점 미만)
    GRADE_4,     // 4등급 (51~60점 미만)
    GRADE_5,     // 5등급 (45~51점 미만)
    COGNITIVE,   // 인지등급 (치매 어르신)
    NONE         // 등급 없음
}
