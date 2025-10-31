package com.caring.caringbackend.global.service;

import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Kakao Map API를 사용한 Geocoding 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoGeocodingService implements GeocodingService {

    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public GeoPoint convertAddressToGeoPoint(Address address) {
        if (address == null) {
            log.warn("Address is null");
            return null;
        }

        // 주소 toString으로 GeoPoint 변환 호출
        return convertAddressStringToGeoPoint(address.toString());
    }

    @Override
    public GeoPoint convertAddressStringToGeoPoint(String fullAddress) {
        if (fullAddress == null || fullAddress.trim().isEmpty()) {
            log.warn("Full address is empty");
            return null;
        }

        try {
            // API 호출 URL 생성
            String url = UriComponentsBuilder.fromUriString(KAKAO_API_URL)
                    .queryParam("query", fullAddress)
                    .build()
                    .toUriString();

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // API 호출
            log.info("Geocoding API 호출: {}", fullAddress);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // 응답 파싱
            return parseGeoPointFromResponse(response.getBody(), fullAddress);

        } catch (Exception e) {
            log.error("Geocoding API 호출 실패: address={}, error={}", fullAddress, e.getMessage());
            return null;
        }
    }

    /**
     * Kakao API 응답에서 위도/경도 추출
     */
    private GeoPoint parseGeoPointFromResponse(String response, String address) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode documents = root.path("documents");

            if (documents.isEmpty()) {
                log.warn("Geocoding 결과 없음: {}", address);
                return null;
            }

            JsonNode firstResult = documents.get(0);
            double latitude = firstResult.path("y").asDouble();
            double longitude = firstResult.path("x").asDouble();

            log.info("Geocoding 성공: {} -> ({}, {})", address, latitude, longitude);

            return GeoPoint.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();

        } catch (Exception e) {
            log.error("Geocoding 응답 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
}
