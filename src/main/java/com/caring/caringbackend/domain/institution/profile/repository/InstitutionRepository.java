package com.caring.caringbackend.domain.institution.profile.repository;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    // 승인 상태별 조회


    // 기관 유형별 조회


    // 기관명 검색


    // 입소 가능 기관 조회


    // 위치 기반 검색
}
