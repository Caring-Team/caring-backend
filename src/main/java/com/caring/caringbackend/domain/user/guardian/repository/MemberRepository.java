package com.caring.caringbackend.domain.user.guardian.repository;

import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.entity.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 👤 회원 Repository 인터페이스
 * <p>
 * Member 엔티티에 대한 데이터 액세스 계층입니다.
 * Spring Data JPA를 활용하여 기본 CRUD 및 커스텀 쿼리 메서드를 제공합니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 회원 단건 조회 (삭제되지 않은 회원만)
     */
    Optional<Member> findByIdAndDeletedFalse(Long id);

    /**
     * 회원 목록 조회 (페이징, 삭제되지 않은 회원만)
     */
    Page<Member> findByDeletedFalse(Pageable pageable);

    /**
     * 역할로 회원 목록 조회 (삭제되지 않은 회원만)
     */
    List<Member> findByRoleAndDeletedFalse(MemberRole role);
}
