package com.caring.caringbackend.domain.tag.repository;

import com.caring.caringbackend.domain.tag.entity.MemberPreferenceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 회원 선호 태그 Repository
 * 
 * @author 윤다인
 * @since 2025-11-19
 */
@Repository
public interface MemberPreferenceTagRepository extends JpaRepository<MemberPreferenceTag, Long> {
    
    /**
     * 회원 ID로 선호 태그 목록 조회
     * 
     * @param memberId 회원 ID
     * @return 선호 태그 목록
     */
    List<MemberPreferenceTag> findByMemberId(Long memberId);
    
    /**
     * 회원 ID로 선호 태그 전체 삭제
     * 
     * @param memberId 회원 ID
     */
    void deleteByMemberId(Long memberId);
}

