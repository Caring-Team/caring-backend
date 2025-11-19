package com.caring.caringbackend.domain.tag.service;

import com.caring.caringbackend.domain.tag.entity.Tag;
import com.caring.caringbackend.domain.tag.entity.TagCategory;
import com.caring.caringbackend.domain.tag.repository.TagRepository;
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
}

