package com.caring.caringbackend.domain.user.elderly.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.caring.caringbackend.IntegrationTestBase;
import com.caring.caringbackend.api.internal.Member.dto.elderly.request.ElderlyProfileCreateRequest;
import com.caring.caringbackend.api.internal.Member.dto.elderly.request.ElderlyProfileCreateRequest.AddressDto;
import com.caring.caringbackend.api.internal.Member.dto.elderly.request.ElderlyProfileUpdateRequest;
import com.caring.caringbackend.api.internal.Member.dto.elderly.response.ElderlyProfileResponse;
import com.caring.caringbackend.domain.user.elderly.entity.ActivityLevel;
import com.caring.caringbackend.domain.user.elderly.entity.CognitiveLevel;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.elderly.entity.LongTermCareGrade;
import com.caring.caringbackend.domain.user.elderly.repository.ElderlyProfileRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.global.service.GeocodingService;
import com.caring.caringbackend.testsupport.TestDataFactory;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("removal")
@Transactional
class ElderlyProfileServiceTest extends IntegrationTestBase {

    @Autowired
    private ElderlyProfileService elderlyProfileService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ElderlyProfileRepository elderlyProfileRepository;

    @MockBean
    private GeocodingService geocodingService;

    @Test
    @DisplayName("장기요양등급이 null이면 NONE으로 저장되고 활동/인지 수준을 그대로 사용한다")
    void createProfile_defaultsGradeToNone() {
        // given
        Member member = memberRepository.save(TestDataFactory.createMember());

        when(geocodingService.convertAddressToGeoPoint(any(Address.class)))
                .thenReturn(new GeoPoint(37.1, 127.1));

        ElderlyProfileCreateRequest request = ElderlyProfileCreateRequest.builder()
                .name("김어르신")
                .gender(member.getGender())
                .birthDate(LocalDate.of(1940, 5, 1))
                .activityLevel(ActivityLevel.HIGH)
                .cognitiveLevel(CognitiveLevel.NORMAL)
                .longTermCareGrade(null)
                .address(AddressDto.builder()
                        .city("서울시 송파구")
                        .street("올림픽로 1")
                        .zipCode("06002")
                        .build())
                .build();

        // when
        ElderlyProfileResponse response = elderlyProfileService.createProfile(member.getId(), request);

        // then
        assertThat(response.getLongTermCareGrade()).isEqualTo(LongTermCareGrade.NONE);
        assertThat(response.getActivityLevel()).isEqualTo(ActivityLevel.HIGH);
        assertThat(response.getCognitiveLevel()).isEqualTo(CognitiveLevel.NORMAL);
        verify(geocodingService, times(1)).convertAddressToGeoPoint(any(Address.class));
    }

    @Test
    @DisplayName("장기요양등급이 있는 상태에서 활동/인지 수준을 입력하면 예외가 발생한다")
    void createProfile_withGradeAndActivity_shouldFail() {
        // given
        Member member = memberRepository.save(TestDataFactory.createMember());
        when(geocodingService.convertAddressToGeoPoint(any(Address.class)))
                .thenReturn(new GeoPoint(37.2, 127.2));

        ElderlyProfileCreateRequest request = ElderlyProfileCreateRequest.builder()
                .name("김어르신")
                .gender(member.getGender())
                .longTermCareGrade(LongTermCareGrade.GRADE_1)
                .activityLevel(ActivityLevel.HIGH)
                .cognitiveLevel(CognitiveLevel.NORMAL)
                .address(AddressDto.builder()
                        .city("서울시 강남구")
                        .street("테헤란로 1")
                        .zipCode("06000")
                        .build())
                .build();

        // expect
        assertThatThrownBy(() -> elderlyProfileService.createProfile(member.getId(), request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("주소를 전달하지 않으면 기존 위치를 유지한다")
    void updateProfile_keepsLocationWhenAddressNull() {
        // given
        Member member = memberRepository.save(TestDataFactory.createMember());
        when(geocodingService.convertAddressToGeoPoint(any(Address.class)))
                .thenReturn(new GeoPoint(37.3, 127.3));

        ElderlyProfileCreateRequest createRequest = ElderlyProfileCreateRequest.builder()
                .name("김어르신")
                .gender(member.getGender())
                .activityLevel(ActivityLevel.HIGH)
                .cognitiveLevel(CognitiveLevel.NORMAL)
                .address(AddressDto.builder()
                        .city("서울시 송파구")
                        .street("올림픽로 1")
                        .zipCode("06002")
                        .build())
                .build();

        ElderlyProfileResponse created = elderlyProfileService.createProfile(member.getId(), createRequest);
        ElderlyProfile stored = elderlyProfileRepository.findById(created.getId()).orElseThrow();
        GeoPoint originalLocation = stored.getLocation();

        Mockito.reset(geocodingService);

        ElderlyProfileUpdateRequest updateRequest = ElderlyProfileUpdateRequest.builder()
                .name("김어르신")
                .gender(stored.getGender())
                .activityLevel(stored.getActivityLevel())
                .cognitiveLevel(stored.getCognitiveLevel())
                .longTermCareGrade(null)
                .notes("변경 없음")
                .build(); // address 생략 -> null

        // when
        ElderlyProfileResponse updated =
                elderlyProfileService.updateProfile(member.getId(), stored.getId(), updateRequest);

        // then
        ElderlyProfile reloaded = elderlyProfileRepository.findById(updated.getId()).orElseThrow();
        assertThat(reloaded.getLocation()).isNotNull();
        assertThat(reloaded.getLocation().getLatitude()).isEqualTo(originalLocation.getLatitude());
        assertThat(reloaded.getLocation().getLongitude()).isEqualTo(originalLocation.getLongitude());
        verify(geocodingService, Mockito.never()).convertAddressToGeoPoint(any(Address.class));
    }
}

