package com.caring.caringbackend.domain.test.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * P6Spy 테스트용 엔티티
 */
@Entity
@Table(name = "test_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private Integer age;

    @Column
    private String email;

    @Builder
    public TestData(String name, String description, Integer age, String email) {
        this.name = name;
        this.description = description;
        this.age = age;
        this.email = email;
    }
}

