package com.caring.caringbackend.domain.test.service;

import com.caring.caringbackend.domain.test.entity.TestData;
import com.caring.caringbackend.domain.test.repository.TestDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 테스트 데이터 서비스
 * AOP 로깅 테스트를 위한 Service Layer
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestDataService {

    private final TestDataRepository testDataRepository;

    /**
     * 전체 테스트 데이터 조회
     */
    public List<TestData> findAll() {
        log.debug("Service: 전체 데이터 조회");
        return testDataRepository.findAll();
    }

    /**
     * ID로 테스트 데이터 조회
     */
    public TestData findById(Long id) {
        log.debug("Service: ID로 데이터 조회 - {}", id);
        return testDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("데이터를 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 이메일로 테스트 데이터 조회
     */
    public TestData findByEmail(String email) {
        log.debug("Service: 이메일로 데이터 조회 - {}", email);
        return testDataRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("데이터를 찾을 수 없습니다. Email: " + email));
    }

    /**
     * 이름으로 테스트 데이터 검색
     */
    public List<TestData> searchByName(String name) {
        log.debug("Service: 이름으로 데이터 검색 - {}", name);
        return testDataRepository.findByNameContaining(name);
    }

    /**
     * 나이 조건으로 테스트 데이터 조회
     */
    public List<TestData> findByAgeGreaterThan(Integer minAge) {
        log.debug("Service: 나이 조건으로 데이터 조회 - {}", minAge);
        return testDataRepository.findByAgeGreaterThan(minAge);
    }

    /**
     * 테스트 데이터 생성
     */
    @Transactional
    public TestData save(TestData testData) {
        log.debug("Service: 데이터 생성 - {}", testData.getName());
        return testDataRepository.save(testData);
    }

    /**
     * 전체 테스트 데이터 삭제
     */
    @Transactional
    public void deleteAll() {
        log.debug("Service: 전체 데이터 삭제");
        testDataRepository.deleteAll();
    }
}

