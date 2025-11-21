package com.caring.caringbackend.global.config;

import com.caring.caringbackend.domain.tag.entity.*;
import com.caring.caringbackend.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 🏷️ 태그 초기화 컴포넌트
 * <p>
 * 애플리케이션 시작 시 Enum으로 정의된 태그들을 DB의 tag 테이블에 자동으로 삽입합니다.
 * 이미 존재하는 태그는 건너뛰고, 새로운 태그만 추가합니다.
 * 
 * @author 윤다인
 * @since 2025-11-19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TagInitializer implements CommandLineRunner {

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("태그 초기화 시작...");

        List<Tag> tagsToSave = new ArrayList<>();
        int displayOrder = 1;

        // 1. SPECIALIZATION (전문/질환) 태그
        log.info("SPECIALIZATION 태그 초기화 중...");
        for (SpecializationTag enumTag : SpecializationTag.values()) {
            if (!tagRepository.existsByCode(enumTag.name())) {
                Tag tag = Tag.builder()
                        .category(TagCategory.SPECIALIZATION)
                        .code(enumTag.name())
                        .name(enumTag.getDescription())
                        .isActive(true)
                        .displayOrder(displayOrder++)
                        .build();
                tagsToSave.add(tag);
            }
        }

        // 2. SERVICE (서비스 유형) 태그
        log.info("SERVICE 태그 초기화 중...");
        displayOrder = 1;
        for (ServiceTag enumTag : ServiceTag.values()) {
            if (!tagRepository.existsByCode(enumTag.name())) {
                Tag tag = Tag.builder()
                        .category(TagCategory.SERVICE)
                        .code(enumTag.name())
                        .name(enumTag.getDescription())
                        .isActive(true)
                        .displayOrder(displayOrder++)
                        .build();
                tagsToSave.add(tag);
            }
        }

        // 3. OPERATION (운영 특성) 태그
        log.info("OPERATION 태그 초기화 중...");
        displayOrder = 1;
        for (OperationTag enumTag : OperationTag.values()) {
            if (!tagRepository.existsByCode(enumTag.name())) {
                Tag tag = Tag.builder()
                        .category(TagCategory.OPERATION)
                        .code(enumTag.name())
                        .name(enumTag.getDescription())
                        .isActive(true)
                        .displayOrder(displayOrder++)
                        .build();
                tagsToSave.add(tag);
            }
        }

        // 4. ENVIRONMENT (환경/시설) 태그
        log.info("ENVIRONMENT 태그 초기화 중...");
        displayOrder = 1;
        for (EnvironmentTag enumTag : EnvironmentTag.values()) {
            if (!tagRepository.existsByCode(enumTag.name())) {
                Tag tag = Tag.builder()
                        .category(TagCategory.ENVIRONMENT)
                        .code(enumTag.name())
                        .name(enumTag.getDescription())
                        .isActive(true)
                        .displayOrder(displayOrder++)
                        .build();
                tagsToSave.add(tag);
            }
        }

        // 5. REVIEW (리뷰 유형) 태그
        log.info("REVIEW 태그 초기화 중...");
        displayOrder = 1;
        for (ReviewTag enumTag : ReviewTag.values()) {
            if (!tagRepository.existsByCode(enumTag.name())) {
                Tag tag = Tag.builder()
                        .category(TagCategory.REVIEW)
                        .code(enumTag.name())
                        .name(enumTag.getDescription())
                        .isActive(true)
                        .displayOrder(displayOrder++)
                        .build();
                tagsToSave.add(tag);
            }
        }

        // 일괄 저장
        if (!tagsToSave.isEmpty()) {
            tagRepository.saveAll(tagsToSave);
            log.info("태그 {} 개 초기화 완료", tagsToSave.size());
        } else {
            log.info("이미 모든 태그가 초기화되어 있습니다.");
        }

        // 전체 태그 수 확인
        long totalCount = tagRepository.count();
        log.info("전체 태그 수: {}", totalCount);
    }
}

