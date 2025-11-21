package com.caring.caringbackend.api.tag.dto.response;

import com.caring.caringbackend.domain.tag.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 태그 목록 응답 DTO
 * 
 * @author 윤다인
 * @since 2025-11-19
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagListResponse {

    /**
     * 태그 목록
     */
    private List<TagResponse> tags;

    /**
     * 전체 태그 수
     */
    private int totalCount;

    /**
     * Tag 리스트를 TagListResponse로 변환
     */
    public static TagListResponse from(List<Tag> tags) {
        List<TagResponse> tagResponses = tags.stream()
                .map(TagResponse::from)
                .collect(Collectors.toList());

        return TagListResponse.builder()
                .tags(tagResponses)
                .totalCount(tagResponses.size())
                .build();
    }
    
    /**
     * TagResponse 리스트를 TagListResponse로 변환
     */
    public static TagListResponse of(List<TagResponse> tagResponses) {
        return TagListResponse.builder()
                .tags(tagResponses)
                .totalCount(tagResponses.size())
                .build();
    }
}

