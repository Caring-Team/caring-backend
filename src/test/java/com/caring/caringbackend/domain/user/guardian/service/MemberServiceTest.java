package com.caring.caringbackend.domain.user.guardian.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.caring.caringbackend.IntegrationTestBase;
import com.caring.caringbackend.api.internal.user.dto.member.response.MemberMyPageResponse;
import com.caring.caringbackend.api.internal.user.dto.member.response.MemberStatisticsResponse;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.domain.review.entity.Review;
import com.caring.caringbackend.domain.review.repository.ReviewRepository;
import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import com.caring.caringbackend.domain.reservation.repository.ReservationRepository;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.elderly.repository.ElderlyProfileRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.global.service.GeocodingService;
import com.caring.caringbackend.testsupport.TestDataFactory;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("removal")
@Transactional
class MemberServiceTest extends IntegrationTestBase {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ElderlyProfileRepository elderlyProfileRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private EntityManager entityManager;

    @MockBean
    private GeocodingService geocodingService;

    @Test
    @DisplayName("회원 통계는 어르신 수와 리뷰 수, 가입일을 반환한다")
    void getStatistics_returnsCounts() {
        // given
        Member member = memberRepository.save(TestDataFactory.createMember());

        ElderlyProfile profile1 = elderlyProfileRepository.save(TestDataFactory.createElderlyProfile(member));
        elderlyProfileRepository.save(TestDataFactory.createElderlyProfile(member));

        ReservationBundle bundle = createReservationBundle(member, profile1);
        reviewRepository.save(TestDataFactory.createReview(bundle.reservation(), member, bundle.institution()));

        // when
        MemberStatisticsResponse statistics = memberService.getStatistics(member.getId());

        // then
        assertThat(statistics.getElderlyCount()).isEqualTo(2);
        assertThat(statistics.getReviewCount()).isEqualTo(1);
        assertThat(statistics.getJoinedAt()).isNotNull();
    }

    @Test
    @DisplayName("마이페이지는 상위 3명의 어르신과 5개의 최근 리뷰만 포함한다")
    void getMyPage_returnsLimitedSummaries() {
        // given
        Member member = memberRepository.save(TestDataFactory.createMember());
        Mockito.when(geocodingService.convertAddressToGeoPoint(Mockito.any()))
                .thenReturn(new GeoPoint(37.5, 127.5));

        List<ElderlyProfile> profiles = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            profiles.add(elderlyProfileRepository.save(TestDataFactory.createElderlyProfile(member)));
        }
        // 하나는 삭제 처리
        profiles.get(0).softDelete();
        elderlyProfileRepository.flush();

        ReservationBundle bundle = createReservationBundle(member, profiles.get(1));
        Reservation reservation = bundle.reservation();

        for (int i = 0; i < 6; i++) {
            Review review = reviewRepository.save(TestDataFactory.createReview(
                    reservation,
                    member,
                    bundle.institution()));
            if (i == 0) {
                review.markReported(); // 신고된 리뷰는 제외
            }
            if (i == 1) {
                review.softDelete(); // 삭제된 리뷰는 제외
            }
        }
        reviewRepository.flush();

        // when
        MemberMyPageResponse myPage = memberService.getMyPage(member.getId());

        // then
        assertThat(myPage.getElderlyProfiles()).hasSize(3); // soft-delete 제외
        assertThat(myPage.getRecentReviews()).hasSize(4); // 신고/삭제 제외 후 최대 5개
        assertThat(myPage.getStatistics().getElderlyCount()).isEqualTo(3);
    }

    private ReservationBundle createReservationBundle(Member member, ElderlyProfile profile) {
        Institution institution = institutionRepository.save(TestDataFactory.createInstitution());
        InstitutionCounsel counsel = TestDataFactory.createInstitutionCounsel(institution);
        entityManager.persist(counsel);
        entityManager.flush();

        InstitutionCounselDetail counselDetail = TestDataFactory.createInstitutionCounselDetail(counsel);
        entityManager.persist(counselDetail);
        entityManager.flush();

        Reservation reservation = reservationRepository.save(
                TestDataFactory.createReservation(counselDetail, member, profile, ReservationStatus.COMPLETED));
        return new ReservationBundle(reservation, institution, counsel);
    }

    private record ReservationBundle(Reservation reservation, Institution institution,
                                     InstitutionCounsel counsel) {
    }
}

