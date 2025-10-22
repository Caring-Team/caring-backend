package com.caring.caringbackend.domain.tag.entity;

import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

// 통합 태그 엔티티
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 태그 카테고리
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagCategory category;

    // 태그 코드 (Enum name)
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    // 태그 이름
    @Column(nullable = false, length = 50)
    private String name;

    // 설명
    @Column(length = 200)
    private String description;

    // 활성화 여부
    @Column(nullable = false)
    private Boolean isActive;

    // 정렬 순서
    private Integer displayOrder;

    @Builder
    public Tag(TagCategory category, String code, String name, String description,
               Boolean isActive, Integer displayOrder) {
        this.category = category;
        this.code = code;
        this.name = name;
        this.description = description;
        this.isActive = isActive != null ? isActive : true;
        this.displayOrder = displayOrder;
    }

    // 정적 팩토리 메서드: Specialization 태그 생성
    public static Tag fromSpecialization(SpecializationTag tag) {
        return Tag.builder()
                .category(TagCategory.SPECIALIZATION)
                .code(tag.name())
                .name(tag.getDescription())
                .build();
    }

    // 정적 팩토리 메서드: Service 태그 생성
    public static Tag fromService(ServiceTag tag) {
        return Tag.builder()
                .category(TagCategory.SERVICE)
                .code(tag.name())
                .name(tag.getDescription())
                .build();
    }

    // 정적 팩토리 메서드: Operation 태그 생성
    public static Tag fromOperation(OperationTag tag) {
        return Tag.builder()
                .category(TagCategory.OPERATION)
                .code(tag.name())
                .name(tag.getDescription())
                .build();
    }

    // 정적 팩토리 메서드: Environment 태그 생성
    public static Tag fromEnvironment(EnvironmentTag tag) {
        return Tag.builder()
                .category(TagCategory.ENVIRONMENT)
                .code(tag.name())
                .name(tag.getDescription())
                .build();
    }

    // 정적 팩토리 메서드: Review 태그 생성
    public static Tag fromReview(ReviewTag tag) {
        return Tag.builder()
                .category(TagCategory.REVIEW)
                .code(tag.name())
                .name(tag.getDescription())
                .build();
    }

    // 비즈니스 로직: 정보 업데이트
    public void updateInfo(String name, String description, Integer displayOrder) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        this.description = description;
        this.displayOrder = displayOrder;
    }
}

