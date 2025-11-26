package com.caring.caringbackend.testsupport;

import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselTimeUnit;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionType;
import com.caring.caringbackend.domain.review.entity.Review;
import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import com.caring.caringbackend.domain.user.elderly.entity.ActivityLevel;
import com.caring.caringbackend.domain.user.elderly.entity.CognitiveLevel;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.elderly.entity.LongTermCareGrade;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.entity.MemberRole;
import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.global.model.Gender;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicLong;

public final class TestDataFactory {

    private static final AtomicLong DUPLICATION_SEQUENCE = new AtomicLong(1000);

    private TestDataFactory() {
    }

    public static Member createMember() {
        long seq = DUPLICATION_SEQUENCE.incrementAndGet();
        return Member.builder()
                .role(MemberRole.USER)
                .name("테스트회원" + seq)
                .phoneNumber("0100000" + seq)
                .duplicationInformation("dup-" + seq)
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(new Address("서울시", "테스트로 " + seq, "000" + seq))
                .location(new GeoPoint(37.0, 127.0))
                .build();
    }

    public static ElderlyProfile createElderlyProfile(Member member) {
        return ElderlyProfile.builder()
                .member(member)
                .name("김어르신")
                .gender(Gender.FEMALE)
                .activityLevel(ActivityLevel.HIGH)
                .cognitiveLevel(CognitiveLevel.NORMAL)
                .longTermCareGrade(LongTermCareGrade.NONE)
                .address(new Address("서울시", "올림픽로", "06002"))
                .location(new GeoPoint(37.1, 127.1))
                .build();
    }

    public static Institution createInstitution() {
        Institution institution = Institution.createInstitution(
                "테스트요양원",
                InstitutionType.NURSING_HOME,
                11111000006L,
                "021234567",
                new Address("서울시", "강남대로", "06100"),
                new GeoPoint(37.4, 127.1),
                "123-45-67890",
                "http://example.com/license.jpg"
        );
        institution.approveInstitution();
        return institution;
    }

    public static InstitutionCounsel createInstitutionCounsel(Institution institution) {
        return InstitutionCounsel.createInstitutionCounsel(
                institution,
                "기본 상담",
                "상담 설명",
                0,
                0,
                CounselTimeUnit.HALF
                );
    }

    public static InstitutionCounselDetail createInstitutionCounselDetail(InstitutionCounsel counsel) {
        return InstitutionCounselDetail.create(
                counsel,
                LocalDate.now().plusDays(1),
                "111111111111111111111111111111111111111111111111" // 모든 시간 예약 가능
        );
    }

    public static Reservation createReservation(InstitutionCounselDetail counselDetail, Member member,
                                                ElderlyProfile profile, ReservationStatus status) {
        return Reservation.builder()
                .counselDetail(counselDetail)
                .member(member)
                .elderlyProfile(profile)
                .reservationTime(LocalTime.of(10, 0))
                .status(status)
                .build();
    }

    public static Review createReview(Reservation reservation, Member member, Institution institution) {
        return Review.builder()
                .reservation(reservation)
                .member(member)
                .institution(institution)
                .content("테스트 리뷰 내용")
                .rating(5)
                .build();
    }
}

