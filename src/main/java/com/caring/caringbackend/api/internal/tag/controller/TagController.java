package com.caring.caringbackend.api.internal.tag.controller;

import com.caring.caringbackend.api.internal.tag.dto.request.TagCreateRequest;
import com.caring.caringbackend.api.internal.tag.dto.request.TagUpdateRequest;
import com.caring.caringbackend.api.internal.tag.dto.response.TagListResponse;
import com.caring.caringbackend.api.internal.tag.dto.response.TagResponse;
import com.caring.caringbackend.domain.tag.entity.Tag;
import com.caring.caringbackend.domain.tag.entity.TagCategory;
import com.caring.caringbackend.domain.tag.service.TagService;
import com.caring.caringbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 태그 관리 Controller
 * 
 * 태그 조회 및 관리 기능을 제공하는 REST API 엔드포인트입니다.
 * 
 * @author 윤다인
 * @since 2025-11-19
 */
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "🏷 Tag", description = "태그 관리 API")
public class TagController {

    private final TagService tagService;

}

