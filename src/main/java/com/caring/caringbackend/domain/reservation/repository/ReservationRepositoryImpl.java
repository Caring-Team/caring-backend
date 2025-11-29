package com.caring.caringbackend.domain.reservation.repository;

import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.caring.caringbackend.domain.institution.counsel.entity.QInstitutionCounsel.institutionCounsel;
import static com.caring.caringbackend.domain.institution.counsel.entity.QInstitutionCounselDetail.institutionCounselDetail;
import static com.caring.caringbackend.domain.institution.profile.entity.QInstitution.institution;
import static com.caring.caringbackend.domain.reservation.entity.QReservation.reservation;
import static com.caring.caringbackend.domain.user.elderly.entity.QElderlyProfile.elderlyProfile;
import static com.caring.caringbackend.domain.user.guardian.entity.QMember.member;

/**
 * 예약 Repository Custom 구현
 */
@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Reservation> findByInstitutionIdWithFilters(
            Long institutionId,
            ReservationStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        // 데이터 조회 쿼리
        List<Reservation> content = queryFactory
                .selectFrom(reservation)
                .join(reservation.counselDetail, institutionCounselDetail).fetchJoin()
                .join(institutionCounselDetail.institutionCounsel, institutionCounsel).fetchJoin()
                .join(institutionCounsel.institution, institution).fetchJoin()
                .join(reservation.member, member).fetchJoin()
                .join(reservation.elderlyProfile, elderlyProfile).fetchJoin()
                .where(
                        institutionIdEq(institutionId),
                        statusEq(status),
                        serviceDateGoe(startDate),
                        serviceDateLoe(endDate)
                )
                .orderBy(
                        institutionCounselDetail.serviceDate.desc(),
                        reservation.startTime.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리 (fetchJoin 제외)
        JPAQuery<Long> countQuery = queryFactory
                .select(reservation.count())
                .from(reservation)
                .join(reservation.counselDetail, institutionCounselDetail)
                .join(institutionCounselDetail.institutionCounsel, institutionCounsel)
                .join(institutionCounsel.institution, institution)
                .where(
                        institutionIdEq(institutionId),
                        statusEq(status),
                        serviceDateGoe(startDate),
                        serviceDateLoe(endDate)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 기관 ID 조건
     */
    private BooleanExpression institutionIdEq(Long institutionId) {
        return institutionId != null ? institution.id.eq(institutionId) : null;
    }

    /**
     * 예약 상태 조건
     */
    private BooleanExpression statusEq(ReservationStatus status) {
        return status != null ? reservation.status.eq(status) : null;
    }

    /**
     * 서비스 날짜 시작일 조건
     */
    private BooleanExpression serviceDateGoe(LocalDate startDate) {
        return startDate != null ? institutionCounselDetail.serviceDate.goe(startDate) : null;
    }

    /**
     * 서비스 날짜 종료일 조건
     */
    private BooleanExpression serviceDateLoe(LocalDate endDate) {
        return endDate != null ? institutionCounselDetail.serviceDate.loe(endDate) : null;
    }
}

