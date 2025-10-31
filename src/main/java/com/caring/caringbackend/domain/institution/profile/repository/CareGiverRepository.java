package com.caring.caringbackend.domain.institution.profile.repository;

import com.caring.caringbackend.domain.institution.profile.entity.CareGiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareGiverRepository extends JpaRepository<CareGiver, Long> {

    // 기관 아이디로 요양사 목록 조회


    // 기관 아이디와 요양사 아이디로 검색


}
