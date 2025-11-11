package com.caring.caringbackend.testsupport;

import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel;
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
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.test.util.ReflectionTestUtils;

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
                "021234567",
                new Address("서울시", "강남대로", "06100"),
                new GeoPoint(37.4, 127.1),
                100,
                true,
                null,
                "09:00-18:00"
        );
        institution.approveInstitution();
        return institution;
    }

    public static InstitutionCounsel createInstitutionCounsel(Institution institution) {
        return InstitutionCounsel.builder()
                .institution(institution)
                .title("기본 상담")
                .description("상담 설명")
                .build();
    }

    public static Reservation createReservation(InstitutionCounsel counsel, Member member,
                                                ElderlyProfile profile, ReservationStatus status) {
        Reservation reservation = Reservation.builder()
                .institutionCounsel(counsel)
                .member(member)
                .elderlyProfile(profile)
                .status(status)
                .build();
        ReflectionTestUtils.setField(reservation, "title", "예약 타이틀");
        ReflectionTestUtils.setField(reservation, "description", "상담 예약입니다");
        ReflectionTestUtils.setField(reservation, "reservationDate", LocalDate.now());
        ReflectionTestUtils.setField(reservation, "reservationTime", "10:00");
        return reservation;
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

