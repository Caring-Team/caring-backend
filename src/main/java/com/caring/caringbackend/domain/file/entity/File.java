package com.caring.caringbackend.domain.file.entity;

import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 파일 엔티티
 * 업로드된 파일의 메타데이터를 관리합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "files")
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 원본 파일명
     */
    @Column(nullable = false)
    private String originalFilename;

    /**
     * 저장된 파일명 (UUID 등)
     */
    @Column(nullable = false, unique = true)
    private String storedFilename;

    /**
     * 파일 URL (S3 또는 로컬 경로)
     */
    @Column(nullable = false, length = 1000)
    private String fileUrl;

    /**
     * 파일 크기 (bytes)
     */
    @Column(nullable = false)
    private Long fileSize;

    /**
     * Content-Type (MIME 타입)
     */
    @Column(nullable = false)
    private String contentType;

    /**
     * 파일 카테고리 (용도 구분)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileCategory category;

    /**
     * 참조 엔티티 ID (기관, 회원 등)
     */
    private Long referenceId;

    /**
     * 참조 엔티티 타입
     */
    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType;

    @Builder(access = AccessLevel.PRIVATE)
    private File(String originalFilename, String storedFilename, String fileUrl,
                 Long fileSize, String contentType, FileCategory category,
                 Long referenceId, ReferenceType referenceType) {
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.category = category;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
    }

    /**
     * 파일 생성 정적 팩토리 메서드
     */
    public static File createFile(String originalFilename, String storedFilename, String fileUrl,
                                   Long fileSize, String contentType, FileCategory category,
                                   Long referenceId, ReferenceType referenceType) {
        return File.builder()
                .originalFilename(originalFilename)
                .storedFilename(storedFilename)
                .fileUrl(fileUrl)
                .fileSize(fileSize)
                .contentType(contentType)
                .category(category)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .build();
    }

    /**
     * 참조 정보 업데이트
     */
    public void updateReference(Long referenceId, ReferenceType referenceType) {
        this.referenceId = referenceId;
        this.referenceType = referenceType;
    }
}
