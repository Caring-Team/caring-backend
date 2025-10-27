package com.caring.caringbackend.domain.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * P6Spy 테스트용 Repository
 */
@Repository
public interface TestDataRepository extends JpaRepository<TestData, Long> {

    Optional<TestData> findByEmail(String email);

    List<TestData> findByNameContaining(String name);

    @Query("SELECT t FROM TestData t WHERE t.age >= :minAge")
    List<TestData> findByAgeGreaterThan(@Param("minAge") Integer minAge);
}

