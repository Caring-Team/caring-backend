package com.caring.caringbackend.domain.user.guardian.repository;

import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.entity.MemberRole;
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
     * 📧 이메일로 회원 조회
     * <p>
     * 회원 가입 시 이메일 중복 체크 및 로그인 시 사용자 찾기에 활용됩니다.
     *
     * @param email 조회할 이메일
     * @return Optional<Member> 이메일에 해당하는 회원 (없으면 empty)
     */
    Optional<Member> findByEmail(String email);

    /**
     * 📧 이메일 존재 여부 확인
     * <p>
     * 회원 가입 시 이메일 중복 검증에 사용됩니다.
     *
     * @param email 확인할 이메일
     * @return 이메일이 이미 존재하는지 여부
     */
    boolean existsByEmail(String email);

    /**
     * 👥 역할로 회원 목록 조회
     * <p>
     * 특정 역할(TEMP_USER, USER)의 회원 목록을 조회할 때 사용됩니다.
     *
     * @param role 조회할 회원 역할
     * @return 해당 역할을 가진 회원 목록
     */
    List<Member> findByRole(MemberRole role);
}
