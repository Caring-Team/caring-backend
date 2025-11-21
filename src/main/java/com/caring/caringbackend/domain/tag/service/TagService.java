package com.caring.caringbackend.domain.tag.service;

import com.caring.caringbackend.api.tag.dto.request.TagCreateRequest;
import com.caring.caringbackend.api.tag.dto.request.TagUpdateRequest;
import com.caring.caringbackend.domain.tag.entity.Tag;
import com.caring.caringbackend.domain.tag.entity.TagCategory;
import com.caring.caringbackend.domain.tag.repository.TagRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Tag 비즈니스 로직을 처리하는 서비스
 * 
 * @author 윤다인
 * @since 2025-11-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    /**
     * 전체 활성화된 태그 목록 조회
     *
     * @return 태그 목록
     */
    public List<Tag> getAllActiveTags() {
        log.debug("전체 활성화된 태그 목록 조회");
        return tagRepository.findByIsActiveTrueOrderByCategoryAscDisplayOrderAsc();
    }

    /**
     * 카테고리별 활성화된 태그 목록 조회
     *
     * @param category 태그 카테고리
     * @return 태그 목록
     */
    public List<Tag> getTagsByCategory(TagCategory category) {
        log.debug("카테고리별 태그 조회: {}", category);
        return tagRepository.findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(category);
    }
    
    /**
     * 태그 생성
     *
     * @param request 태그 생성 요청
     * @return 생성된 태그
     */
    @Transactional
    public Tag createTag(TagCreateRequest request) {
        // 중복 코드 확인
        if (tagRepository.existsByCode(request.getCode())) {
            throw new BusinessException(ErrorCode.TAG_ALREADY_EXISTS);
        }
        
        Tag tag = Tag.builder()
                .category(request.getCategory())
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .displayOrder(request.getDisplayOrder())
                .build();
        
        Tag savedTag = tagRepository.save(tag);
        log.info("태그 생성 완료: tagId={}, code={}, name={}", savedTag.getId(), savedTag.getCode(), savedTag.getName());
        return savedTag;
    }
    
    /**
     * 태그 수정
     *
     * @param tagId 태그 ID
     * @param request 태그 수정 요청
     * @return 수정된 태그
     */
    @Transactional
    public Tag updateTag(Long tagId, TagUpdateRequest request) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TAG_NOT_FOUND));
        
        tag.updateInfo(
                request.getName(),
                request.getDescription(),
                request.getIsActive(),
                request.getDisplayOrder()
        );
        
        log.info("태그 수정 완료: tagId={}, name={}", tagId, tag.getName());
        return tag;
    }
    
    /**
     * 태그 삭제 (비활성화)
     *
     * @param tagId 태그 ID
     */
    @Transactional
    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TAG_NOT_FOUND));
        
        // 비활성화 처리 (Soft Delete)
        tag.updateInfo(null, null, false, null);
        
        log.info("태그 비활성화 완료: tagId={}, code={}", tagId, tag.getCode());
    }
}

