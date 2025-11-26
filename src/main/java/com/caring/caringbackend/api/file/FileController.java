package com.caring.caringbackend.api.file;

import com.caring.caringbackend.domain.file.service.FileService;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.caring.caringbackend.global.exception.ErrorCode.FILE_IS_EMPTY;
import static com.caring.caringbackend.global.exception.ErrorCode.FILE_SIZE_EXCEEDED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
@Tag(name = "📁 File", description = "파일 업로드 API")
@Hidden
public class FileController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "파일 업로드", description = "파일을 업로드하고, 업로드된 파일의 URL을 반환합니다.")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        validate(file);
        String url = fileService.uploadFile(file, "uploads");
        return ApiResponse.success(url);
    }

    // =============== private methods ===============

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
