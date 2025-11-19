package com.caring.caringbackend.domain.tag.repository;

import com.caring.caringbackend.domain.tag.entity.Tag;
import com.caring.caringbackend.domain.tag.entity.TagCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 태그 Repository 인터페이스
 * <p>
 * Tag 엔티티에 대한 데이터 액세스 계층입니다.
 *
 * @author 윤다인
 * @since 2025-11-18
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 태그 ID 목록으로 태그 조회
     *
     * @param ids 태그 ID 목록
     * @return 태그 목록
     */
    List<Tag> findAllByIdIn(List<Long> ids);

    /**
     * 코드와 카테고리로 태그 조회
     *
     * @param code 태그 코드
     * @param category 태그 카테고리
     * @return 태그
     */
    Optional<Tag> findByCodeAndCategory(String code, TagCategory category);

    /**
     * 카테고리별 활성화된 태그 목록 조회
     *
     * @param category 태그 카테고리
     * @return 태그 목록
     */
    List<Tag> findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(TagCategory category);

    /**
     * 태그 코드 존재 여부 확인
     *
     * @param code 태그 코드
     * @return 존재 여부
     */
    boolean existsByCode(String code);

    /**
     * 전체 활성화된 태그 목록 조회
     *
     * @return 태그 목록
     */
    List<Tag> findByIsActiveTrueOrderByCategoryAscDisplayOrderAsc();
}

