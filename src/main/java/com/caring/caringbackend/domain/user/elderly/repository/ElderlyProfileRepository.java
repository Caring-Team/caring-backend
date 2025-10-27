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
     * 👤 회원 ID로 어르신 프로필 목록 조회
     * <p>
     * 특정 회원이 등록한 모든 어르신 프로필을 조회합니다.
     *
     * @param memberId 회원 ID
     * @return 해당 회원의 어르신 프로필 목록
     */
    List<ElderlyProfile> findByMemberId(Long memberId);

    /**
     * 🔍 특정 어르신 프로필 조회 (소유자 검증 포함)
     * <p>
     * 프로필 ID와 회원 ID를 모두 확인하여,
     * 해당 회원이 소유한 어르신 프로필인지 검증합니다.
     *
     * @param id 어르신 프로필 ID
     * @param memberId 회원 ID
     * @return Optional<ElderlyProfile> 조건에 맞는 어르신 프로필
     */
    Optional<ElderlyProfile> findByIdAndMemberId(Long id, Long memberId);

    /**
     * 📊 특정 회원의 어르신 프로필 수 조회
     * <p>
     * 회원이 등록한 어르신 프로필의 개수를 카운트합니다.
     * 프로필 등록 제한 체크 등에 활용 가능합니다.
     *
     * @param memberId 회원 ID
     * @return 해당 회원의 어르신 프로필 개수
     */
    long countByMemberId(Long memberId);
}
