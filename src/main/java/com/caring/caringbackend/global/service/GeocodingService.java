package com.caring.caringbackend.global.service;

import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;

/**
 * Geocoding 서비스 인터페이스
 * 주소를 위도/경도 좌표로 변환하는 기능을 제공합니다.
 */
public interface GeocodingService {

    /**
     * 주소를 위도/경도 좌표로 변환
     *
     * @param address 변환할 주소 객체
     * @return GeoPoint 위도/경도 좌표 (변환 실패 시 null)
     */
    GeoPoint convertAddressToGeoPoint(Address address);

    /**
     * 주소 문자열을 위도/경도 좌표로 변환
     *
     * @param fullAddress 전체 주소 문자열
     * @return GeoPoint 위도/경도 좌표 (변환 실패 시 null)
     */
    GeoPoint convertAddressStringToGeoPoint(String fullAddress);
}

