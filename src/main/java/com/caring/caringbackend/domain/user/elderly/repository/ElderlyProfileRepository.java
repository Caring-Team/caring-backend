package com.caring.caringbackend.domain.user.elderly.repository;

import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 👵 어르신 프로필 Repository 인터페이스
 * <p>
 * ElderlyProfile 엔티티에 대한 데이터 액세스 계층입니다.
 * 회원별 어르신 프로필 관리에 필요한 쿼리 메서드를 제공합니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Repository
public interface ElderlyProfileRepository extends JpaRepository<ElderlyProfile, Long> {

    /**
     * 회원 ID로 어르신 프로필 목록 조회 (삭제되지 않은 프로필만)
     */
    List<ElderlyProfile> findByMemberIdAndDeletedFalse(Long memberId);

    /**
     * 특정 어르신 프로필 조회 (소유자 검증 포함, 삭제되지 않은 프로필만)
     */
    Optional<ElderlyProfile> findByIdAndMemberIdAndDeletedFalse(Long id, Long memberId);

    /**
     * 특정 회원의 어르신 프로필 수 조회 (삭제되지 않은 프로필만)
     */
    long countByMemberIdAndDeletedFalse(Long memberId);
}
