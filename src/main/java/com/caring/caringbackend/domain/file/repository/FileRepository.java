package com.caring.caringbackend.domain.file.repository;

import com.caring.caringbackend.domain.file.entity.File;
import com.caring.caringbackend.domain.file.entity.FileCategory;
import com.caring.caringbackend.domain.file.entity.ReferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    /**
     * 저장된 파일명으로 조회
     */
    Optional<File> findByStoredFilename(String storedFilename);

    /**
     * 참조 정보로 파일 목록 조회
     */
    List<File> findByReferenceIdAndReferenceType(Long referenceId, ReferenceType referenceType);

    /**
     * 참조 정보와 카테고리로 파일 목록 조회
     */
    List<File> findByReferenceIdAndReferenceTypeAndCategory(
            Long referenceId,
            ReferenceType referenceType,
            FileCategory category
    );

    /**
     * 참조 정보와 카테고리로 단일 파일 조회 (사업자 등록증 등)
     */
    Optional<File> findFirstByReferenceIdAndReferenceTypeAndCategory(
            Long referenceId,
            ReferenceType referenceType,
            FileCategory category
    );

    /**
     * 참조 정보로 파일 삭제
     */
    void deleteByReferenceIdAndReferenceType(Long referenceId, ReferenceType referenceType);
}

