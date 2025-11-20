package com.caring.caringbackend.domain.institution.advertisement.repository;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisementRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.caring.caringbackend.domain.institution.advertisement.entity.QInstitutionAdvertisementRequest.institutionAdvertisementRequest;

@RequiredArgsConstructor
public class InstitutionAdvertisementRequestRepositoryImpl implements InstitutionAdvertisementRequestCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 기관 ID로 광고 신청 목록 조회 (필터 적용)
     */
    @Override
    public List<InstitutionAdvertisementRequest> findByInstitutionIdWithFilters(
            Long institutionId,
            AdvertisementStatus status,
            AdvertisementType type
    ) {
        return queryFactory
                .selectFrom(institutionAdvertisementRequest)
                .where(
                        institutionIdEq(institutionId),
                        statusEq(status),
                        typeEq(type)
                )
                .orderBy(institutionAdvertisementRequest.createdAt.desc())
                .fetch();
    }

    /**
     * 관리자용 전체 광고 신청 목록 조회 (필터 + 페이징)
     */
    @Override
    public Page<InstitutionAdvertisementRequest> findAllWithFilters(
            AdvertisementStatus status,
            AdvertisementType type,
            Pageable pageable
    ) {
        // 데이터 조회 쿼리
        List<InstitutionAdvertisementRequest> content = queryFactory
                .selectFrom(institutionAdvertisementRequest)
                .where(
                        statusEq(status),
                        typeEq(type)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(institutionAdvertisementRequest.createdAt.desc())
                .fetch();

        // Count 쿼리 (최적화)
        JPAQuery<Long> countQuery = queryFactory
                .select(institutionAdvertisementRequest.count())
                .from(institutionAdvertisementRequest)
                .where(
                        statusEq(status),
                        typeEq(type)
                );

        // Page 객체 생성
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 기관 ID 조건 (필수)
     */
    private BooleanExpression institutionIdEq(Long institutionId) {
        return institutionId != null ? institutionAdvertisementRequest.institution.id.eq(institutionId) : null;
    }

    /**
     * 상태 조건 (선택)
     */
    private BooleanExpression statusEq(AdvertisementStatus status) {
        return status != null ? institutionAdvertisementRequest.status.eq(status) : null;
    }

    /**
     * 광고 유형 조건 (선택)
     */
    private BooleanExpression typeEq(AdvertisementType type) {
        return type != null ? institutionAdvertisementRequest.type.eq(type) : null;
    }
}

