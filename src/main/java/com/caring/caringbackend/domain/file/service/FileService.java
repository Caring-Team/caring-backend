package com.caring.caringbackend.domain.file.service;

import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

import static com.caring.caringbackend.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${app.aws.s3.bucket-name}")
    private String bucketName;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private final S3Client s3Client;

    // 파일 업로드 및 db 저장 로직 구현
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

        return "https://" + bucketName + ".s3." + s3Client.serviceClientConfiguration().region().id() + ".amazon.com/" + key;
    }

    // ============== private methods ==============

    private static void validate(MultipartFile file) {
        validateExist(file);
        validateSize(file);
    }

    private static void validateExist(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(FILE_IS_EMPTY);
        }
    }

    private static void validateSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(FILE_SIZE_EXCEEDED);
        }
    }
}
