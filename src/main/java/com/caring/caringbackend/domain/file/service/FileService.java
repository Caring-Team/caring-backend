package com.caring.caringbackend.domain.file.service;

import com.caring.caringbackend.domain.file.entity.File;
import com.caring.caringbackend.domain.file.entity.FileCategory;
import com.caring.caringbackend.domain.file.entity.ReferenceType;
import com.caring.caringbackend.domain.file.repository.FileRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static com.caring.caringbackend.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${app.aws.s3.bucket-name}")
    private String bucketName;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Duration PRESIGNED_URL_DURATION = Duration.ofHours(1); // PreSigned URL 유효시간: 1시간
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final FileRepository fileRepository;

    /**
     * 파일 업로드 (URL만 반환, DB 저장 안 함) - 기존 코드 호환용
     */
    public String uploadFile(MultipartFile file, String dir) {
        validate(file);

        String key = dir + "/" + UUID.randomUUID();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new BusinessException(FILE_UPLOAD_FAILED);
        }

        return buildFileUrl(key);
    }

    /**
     * 파일 업로드 및 DB 저장 (File 엔티티 반환)
     */
    @Transactional
    public File uploadFileWithMetadata(
            MultipartFile file,
            FileCategory category,
            Long referenceId,
            ReferenceType referenceType
    ) {
        validate(file);

        // S3에 파일 업로드
        String storedFilename = UUID.randomUUID().toString();
        String dir = category.name().toLowerCase().replace("_", "-");
        String key = dir + "/" + storedFilename;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", file.getOriginalFilename(), e);
            throw new BusinessException(FILE_UPLOAD_FAILED);
        }

        // File 엔티티 생성 및 저장
        String fileUrl = buildFileUrl(key);
        File fileEntity = File.createFile(
                file.getOriginalFilename(),
                storedFilename,
                fileUrl,
                file.getSize(),
                file.getContentType(),
                category,
                referenceId,
                referenceType
        );

        return fileRepository.save(fileEntity);
    }

    /**
     * 참조 정보 업데이트
     */
    @Transactional
    public void updateFileReference(Long fileId, Long referenceId, ReferenceType referenceType) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(FILE_NOT_FOUND));

        file.updateReference(referenceId, referenceType);
        log.info("파일 참조 정보 업데이트 - FileId: {}, ReferenceId: {}, Type: {}",
                fileId, referenceId, referenceType);
    }

    /**
     * 참조 정보로 파일 목록 조회
     */
    @Transactional(readOnly = true)
    public List<File> getFilesByReference(Long referenceId, ReferenceType referenceType) {
        return fileRepository.findByReferenceIdAndReferenceType(referenceId, referenceType);
    }

    /**
     * 참조 정보와 카테고리로 파일 조회
     */
    @Transactional(readOnly = true)
    public List<File> getFilesByReferenceAndCategory(
            Long referenceId,
            ReferenceType referenceType,
            FileCategory category
    ) {
        return fileRepository.findByReferenceIdAndReferenceTypeAndCategory(
                referenceId, referenceType, category
        );
    }

    /**
     * 단일 파일 조회
     */
    @Transactional(readOnly = true)
    public File getFileByReferenceAndCategory(
            Long referenceId,
            ReferenceType referenceType,
            FileCategory category
    ) {
        return fileRepository.findFirstByReferenceIdAndReferenceTypeAndCategory(
                referenceId, referenceType, category
        ).orElse(null);
    }

    /**
     * 사업자등록증 업로드 및 기관과 연결 (편의 메서드)
     *
     * @param file          사업자등록증 파일
     * @param institutionId 기관 ID
     * @return 저장된 File 엔티티
     */
    @Transactional
    public File uploadAndLinkBusinessLicense(MultipartFile file, Long institutionId) {
        validateFileFormat(file);
        return uploadFileWithMetadata(
                file,
                FileCategory.BUSINESS_LICENSE,
                institutionId,
                ReferenceType.INSTITUTION
        );
    }

    @Transactional
    public File uploadCareGiverPhoto(MultipartFile file, Long careGiverId) {
        validateFileFormat(file);
        return uploadFileWithMetadata(
                file,
                FileCategory.CAREGIVER_PHOTO,
                careGiverId,
                ReferenceType.CAREGIVER
        );
    }

    private static void validateFileFormat(MultipartFile file) {
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new BusinessException(INVALID_FILE_FORMAT);
        }
    }

    /**
     * 파일 삭제
     */
    @Transactional
    public void deleteFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(FILE_NOT_FOUND));

        // TODO: S3에서 파일 삭제 (필요 시 구현)

        fileRepository.delete(file);
        log.info("파일 삭제 완료 - FileId: {}, 파일명: {}", fileId, file.getOriginalFilename());
    }

    /**
     * S3 URL을 PreSigned URL로 변환
     *
     * @param s3Url S3 URL
     * @return PreSigned URL (1시간 유효)
     */
    public String generatePresignedUrl(String s3Url) {
        if (s3Url == null || s3Url.isEmpty()) {
            return null;
        }

        try {
            // S3 URL에서 key 추출
            String key = extractKeyFromUrl(s3Url);

            // PreSigned URL 생성
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(PRESIGNED_URL_DURATION)
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("PreSigned URL 생성 실패: {}", s3Url, e);
            return s3Url; // 실패 시 원본 URL 반환
        }
    }

    /**
     * S3 URL에서 key 추출
     */
    private String extractKeyFromUrl(String s3Url) {
        int keyStartIndex = s3Url.indexOf(".amazonaws.com/");

        if (keyStartIndex != -1) {
            return s3Url.substring(keyStartIndex + ".amazonaws.com/".length());
        }

        // URL 형식이 다를 경우 처리
        throw new IllegalArgumentException("Invalid S3 URL format: " + s3Url);
    }

    // ============== private methods ==============

    private String buildFileUrl(String key) {
        return "https://" + bucketName + ".s3." +
                s3Client.serviceClientConfiguration().region().id() +
                ".amazonaws.com/" + key;
    }

    private static void validate(MultipartFile file) {
        validateExist(file);
        validateSize(file);
    }

    private static void validateExist(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(FILE_IS_EMPTY);
        }
    }

    private static void validateSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(FILE_SIZE_EXCEEDED);
        }
    }
}
