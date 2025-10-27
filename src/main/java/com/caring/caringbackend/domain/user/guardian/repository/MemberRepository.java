package com.caring.caringbackend.domain.user.guardian.repository;

import com.caring.caringbackend.domain.user.guardian.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Optional<Member> findByPhoneNumber(String phone);
    public Optional<Member> findByDuplicationInformation(String duplicationInformation);
    public boolean existsByDuplicationInformation(String duplicationInformation);
}
