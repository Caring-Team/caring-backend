package com.caring.caringbackend.domain.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.caring.caringbackend.IntegrationTestBase;
import com.caring.caringbackend.api.user.dto.review.request.ReviewCreateRequest;
import com.caring.caringbackend.api.user.dto.review.request.ReviewReportRequest;
import com.caring.caringbackend.api.user.dto.review.request.ReviewUpdateRequest;
import com.caring.caringbackend.api.user.dto.review.response.ReviewResponse;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import com.caring.caringbackend.domain.reservation.repository.ReservationRepository;
import com.caring.caringbackend.domain.review.entity.ReportReason;
import com.caring.caringbackend.domain.review.entity.Review;
import com.caring.caringbackend.domain.review.entity.ReviewReport;
import com.caring.caringbackend.domain.review.repository.ReviewReportRepository;
import com.caring.caringbackend.domain.review.repository.ReviewRepository;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.elderly.repository.ElderlyProfileRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.testsupport.TestDataFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ReviewServiceTest extends IntegrationTestBase {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ElderlyProfileRepository elderlyProfileRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewReportRepository reviewReportRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("완료된 예약에 대해 리뷰를 작성할 수 있다")
    void createReview_success() {
        // given
        Member member = memberRepository.save(TestDataFactory.createMember());
        ElderlyProfile profile = TestDataFactory.createElderlyProfile(member);
        elderlyProfileRepository.save(profile);

        Institution institution = institutionRepository.save(TestDataFactory.createInstitution());
        InstitutionCounsel counsel = TestDataFactory.createInstitutionCounsel(institution);
        entityManager.persist(counsel);
        entityManager.flush();

        InstitutionCounselDetail counselDetail = TestDataFactory.createInstitutionCounselDetail(counsel);
        entityManager.persist(counselDetail);
        entityManager.flush();

        Reservation reservation = reservationRepository.save(
                TestDataFactory.createReservation(counselDetail, member, profile, ReservationStatus.COMPLETED));

        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .reservationId(reservation.getId())
                .content("서비스가 매우 만족스러웠습니다.")
                .rating(5)
                .build();

        // when
        ReviewResponse response = reviewService.createReview(member.getId(), request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getInstitution().getId()).isEqualTo(institution.getId());
    }

    @Test
    @DisplayName("같은 예약에 대해 리뷰를 중복 작성할 수 없다")
    void createReview_duplicateNotAllowed() {
        // given
        Member member = memberRepository.save(TestDataFactory.createMember());
        ElderlyProfile profile = elderlyProfileRepository.save(TestDataFactory.createElderlyProfile(member));
        Institution institution = institutionRepository.save(TestDataFactory.createInstitution());
        InstitutionCounsel counsel = TestDataFactory.createInstitutionCounsel(institution);
        entityManager.persist(counsel);
        entityManager.flush();

        InstitutionCounselDetail counselDetail = TestDataFactory.createInstitutionCounselDetail(counsel);
        entityManager.persist(counselDetail);
        entityManager.flush();

        Reservation reservation = reservationRepository.save(
                TestDataFactory.createReservation(counselDetail, member, profile, ReservationStatus.COMPLETED));

        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .reservationId(reservation.getId())
                .content("만족스러운 서비스였습니다.")
                .rating(4)
                .build();

        reviewService.createReview(member.getId(), request);

        // expect
        assertThatThrownBy(() -> reviewService.createReview(member.getId(), request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("30일 이내 작성한 리뷰는 수정이 가능하다")
    void updateReview_within30Days() {
        // given
        Review review = prepareReview();
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
                .content("수정된 리뷰 내용")
                .rating(3)
                .build();

        // when
        ReviewResponse response = reviewService.updateReview(review.getMember().getId(),
                review.getId(), request);

        // then
        assertThat(response.getRating()).isEqualTo(3);
        assertThat(response.getContent()).isEqualTo("수정된 리뷰 내용");
    }

    @Test
    @DisplayName("작성 후 30일이 지난 리뷰는 수정할 수 없다")
    void updateReview_after30Days_shouldFail() {
        // given
        Review review = prepareReview();
        // created_at 은 BaseEntity에서 updatable=false 이므로 네이티브 쿼리로 직접 갱신한다
        entityManager.createNativeQuery(
                "UPDATE review SET created_at = ?1 WHERE id = ?2")
                .setParameter(1, java.sql.Timestamp.valueOf(LocalDateTime.now().minusDays(31)))
                .setParameter(2, review.getId())
                .executeUpdate();
        entityManager.clear();

        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
                .content("수정 시도")
                .rating(2)
                .build();

        // expect
        assertThatThrownBy(() -> reviewService.updateReview(review.getMember().getId(),
                review.getId(), request)).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("타인이 작성한 리뷰를 신고하면 reported 플래그가 true가 된다")
    void reportReview_success() {
        // given
        Review review = prepareReview();
        Member reporter = memberRepository.save(TestDataFactory.createMember());

        ReviewReportRequest request = ReviewReportRequest.builder()
                .reportReason(ReportReason.SPAM)
                .description("광고 리뷰로 판단됨")
                .build();

        // when
        reviewService.reportReview(reporter.getId(), review.getId(), request);

        // then
        Review reloaded = reviewRepository.findById(review.getId()).orElseThrow();
        assertThat(reloaded.isReported()).isTrue();

        ReviewReport report = reviewReportRepository.findByReviewIdAndMemberIdAndDeletedFalse(
                        review.getId(), reporter.getId())
                .orElseThrow();
        assertThat(report.getReason()).isEqualTo(ReportReason.SPAM);
    }

    @Test
    @DisplayName("자신의 리뷰는 신고할 수 없다")
    void reportReview_selfNotAllowed() {
        // given
        Review review = prepareReview();
        ReviewReportRequest request = ReviewReportRequest.builder()
                .reportReason(ReportReason.SPAM)
                .build();

        // expect
        assertThatThrownBy(() -> reviewService.reportReview(review.getMember().getId(),
                review.getId(), request)).isInstanceOf(BusinessException.class);
    }

    private Review prepareReview() {
        Member member = memberRepository.save(TestDataFactory.createMember());
        ElderlyProfile profile = elderlyProfileRepository.save(TestDataFactory.createElderlyProfile(member));
        Institution institution = institutionRepository.save(TestDataFactory.createInstitution());
        InstitutionCounsel counsel = TestDataFactory.createInstitutionCounsel(institution);
        entityManager.persist(counsel);
        entityManager.flush();

        InstitutionCounselDetail counselDetail = TestDataFactory.createInstitutionCounselDetail(counsel);
        entityManager.persist(counselDetail);
        entityManager.flush();

        Reservation reservation = reservationRepository.save(
                TestDataFactory.createReservation(counselDetail, member, profile, ReservationStatus.COMPLETED));

        Review review = reviewRepository.save(TestDataFactory.createReview(reservation, member, institution));

        // createdAt이 null일 경우를 대비해 즉시 로딩
        if (review.getCreatedAt() == null) {
            ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now());
        }
        return review;
    }
}

