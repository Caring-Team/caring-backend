package com.caring.caringbackend.api.user.dto.member.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 회원 선호 태그 설정 요청 DTO
 * 
 * @author 윤다인
 * @since 2025-11-19
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberPreferenceTagRequest {
    
    /**
     * 선호 태그 ID 목록 (최대 10개)
     */
    @NotNull(message = "태그 ID 목록은 필수입니다")
    @Size(max = 10, message = "선호 태그는 최대 10개까지 선택할 수 있습니다")
    private List<Long> tagIds;
}

