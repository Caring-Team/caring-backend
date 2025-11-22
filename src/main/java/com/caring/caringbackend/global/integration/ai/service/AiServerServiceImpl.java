package com.caring.caringbackend.global.integration.ai.service;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.global.integration.ai.config.AiServerProperties;
import com.caring.caringbackend.global.integration.ai.dto.InstitutionEmbeddingRequest;
import com.caring.caringbackend.global.integration.ai.dto.InstitutionEmbeddingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 서버 통신 서비스 구현체
 *
 * @author 나의찬
 * @since 2025-11-22
 */
@Slf4j
@Service
public class AiServerServiceImpl implements AiServerService {

    private final RestTemplate restTemplate;
    private final AiServerProperties aiServerProperties;

    public AiServerServiceImpl(
            @Qualifier("aiServerRestTemplate") RestTemplate restTemplate,
            AiServerProperties aiServerProperties) {
        this.restTemplate = restTemplate;
        this.aiServerProperties = aiServerProperties;
    }

    /**
     * 기관 데이터를 AI 서버로 전송하여 임베딩 벡터로 저장
     *
     * @param institution 기관 엔티티
     * @return 성공 여부
     */
    @Override
    public boolean sendInstitutionEmbedding(Institution institution) {
        try {
            // 기관 엔티티를 AI 서버 요청 DTO로 변환
            InstitutionEmbeddingRequest request = convertToEmbeddingRequest(institution);

            // HTTP 헤더 설정
            HttpHeaders headers = createHeaders();
            HttpEntity<InstitutionEmbeddingRequest> entity = new HttpEntity<>(request, headers);

            // AI 서버로 POST 요청
            log.info("AI 서버로 기관 임베딩 요청 전송 시작: institutionId={}, name={}",
                    institution.getId(), institution.getName());

            ResponseEntity<InstitutionEmbeddingResponse> response = restTemplate.exchange(
                    aiServerProperties.getInstitutionEmbeddingPath(),
                    HttpMethod.POST,
                    entity,
                    InstitutionEmbeddingResponse.class
            );

            // 응답 검증
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                InstitutionEmbeddingResponse body = response.getBody();
                log.info("AI 서버 기관 임베딩 성공: institutionId={}, message={}, vectorDimension={}",
                        institution.getId(), body.getMessage(), body.getVectorDimension());
                return body.getSuccess();
            }

            log.warn("AI 서버 기관 임베딩 실패: institutionId={}, status={}",
                    institution.getId(), response.getStatusCode());
            return false;

        } catch (RestClientException e) {
            log.error("AI 서버 통신 오류 (기관 임베딩): institutionId={}, error={}",
                    institution.getId(), e.getMessage(), e);
            // 임베딩 실패가 기관 승인을 막지 않도록 false 반환하되 로그만 남김
            return false;
        } catch (Exception e) {
            log.error("기관 임베딩 처리 중 예상치 못한 오류: institutionId={}, error={}",
                    institution.getId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 기관 임베딩 데이터 삭제 (기관 삭제 시)
     *
     * @param institutionId 기관 ID
     * @return 성공 여부
     */
    @Override
    public boolean deleteInstitutionEmbedding(Long institutionId) {
        try {
            // HTTP 헤더 설정
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // AI 서버로 DELETE 요청
            log.info("AI 서버로 기관 임베딩 삭제 요청 전송: institutionId={}", institutionId);

            String deleteUrl = aiServerProperties.getInstitutionEmbeddingPath() + "/" + institutionId;

            ResponseEntity<InstitutionEmbeddingResponse> response = restTemplate.exchange(
                    deleteUrl,
                    HttpMethod.DELETE,
                    entity,
                    InstitutionEmbeddingResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("AI 서버 기관 임베딩 삭제 성공: institutionId={}", institutionId);
                return true;
            }

            log.warn("AI 서버 기관 임베딩 삭제 실패: institutionId={}, status={}",
                    institutionId, response.getStatusCode());
            return false;

        } catch (RestClientException e) {
            log.error("AI 서버 통신 오류 (기관 임베딩 삭제): institutionId={}, error={}",
                    institutionId, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("기관 임베딩 삭제 중 예상치 못한 오류: institutionId={}, error={}",
                    institutionId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 기관 엔티티를 AI 서버 요청 DTO로 변환
     */
    private InstitutionEmbeddingRequest convertToEmbeddingRequest(Institution institution) {
        // 주소 문자열 생성
        String address = String.format("%s %s %s",
                institution.getAddress().getCity(),
                institution.getAddress().getStreet(),
                institution.getAddress().getZipCode());

        // 태그를 카테고리별로 분류
        Map<String, List<String>> tagsByCategory = institution.getTags().stream()
                .collect(Collectors.groupingBy(
                        institutionTag -> institutionTag.getTag().getCategory().name(),
                        Collectors.mapping(
                                institutionTag -> institutionTag.getTag().getName(),
                                Collectors.toList()
                        )
                ));

        return InstitutionEmbeddingRequest.builder()
                .institutionId(institution.getId())
                .name(institution.getName())
                .institutionType(institution.getInstitutionType().name())
                .address(address)
                .latitude(institution.getLocation().getLatitude())
                .longitude(institution.getLocation().getLongitude())
                .bedCount(institution.getBedCount())
                .monthlyBaseFee(institution.getPriceInfo() != null ?
                        institution.getPriceInfo().getMonthlyBaseFee() : null)
                .admissionFee(institution.getPriceInfo() != null ?
                        institution.getPriceInfo().getAdmissionFee() : null)
                .monthlyMealCost(institution.getPriceInfo() != null ?
                        institution.getPriceInfo().getMonthlyMealCost() : null)
                .openingHours(institution.getOpeningHours())
                .specializedDiseases(tagsByCategory.getOrDefault("SPECIALIZATION", List.of()))
                .serviceTypes(tagsByCategory.getOrDefault("SERVICE", List.of()))
                .operationalFeatures(tagsByCategory.getOrDefault("OPERATION", List.of()))
                .facilityFeatures(tagsByCategory.getOrDefault("ENVIRONMENT", List.of()))
                .description(institution.getDescription())
                .isAdmissionAvailable(institution.getIsAdmissionAvailable())
                .build();
    }

    /**
     * HTTP 헤더 생성
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // API 키가 설정되어 있으면 추가
        if (aiServerProperties.getApiKey() != null && !aiServerProperties.getApiKey().isEmpty()) {
            headers.set("X-API-Key", aiServerProperties.getApiKey());
        }

        return headers;
    }
}

