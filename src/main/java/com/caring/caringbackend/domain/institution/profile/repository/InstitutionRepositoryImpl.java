package com.caring.caringbackend.domain.institution.profile.repository;

import com.caring.caringbackend.api.institution.dto.request.InstitutionSearchFilter;
import com.caring.caringbackend.domain.institution.profile.entity.ApprovalStatus;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionType;
import com.caring.caringbackend.domain.institution.profile.entity.QInstitution;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Institution Repository Custom 구현체
 *
 * QueryDSL을 사용한 동적 쿼리 구현
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class InstitutionRepositoryImpl implements InstitutionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QInstitution institution = QInstitution.institution;

    /**
     * QueryDSL을 사용한 동적 검색 (BooleanBuilder 방식)
     *
     * 장점: 조건을 동적으로 추가 가능
     */
    @Override
    public Page<Institution> searchWithQueryDsl(InstitutionSearchFilter filter, Pageable pageable) {
        // WHERE 조건 동적 생성
        BooleanBuilder builder = createWhereClause(filter);

        // 데이터 조회 쿼리
        JPAQuery<Institution> query = queryFactory
                .selectFrom(institution)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 동적 정렬 적용
        applySort(query, pageable);

        List<Institution> content = query.fetch();

        // COUNT 쿼리 (별도 실행으로 성능 최적화)
        Long total = queryFactory
                .select(institution.count())
                .from(institution)
                .where(builder)
                .fetchOne();

        log.debug("QueryDSL 검색 완료: total={}, page={}, size={}",
                total, pageable.getPageNumber(), content.size());

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    /**
     * QueryDSL을 사용한 동적 검색 (BooleanExpression 방식 - 권장)
     *
     * 장점:
     * - 재사용 가능
     * - 가독성 높음
     * - null 안전
     */
    public Page<Institution> searchWithBooleanExpression(InstitutionSearchFilter filter, Pageable pageable) {
        List<Institution> content = queryFactory
                .selectFrom(institution)
                .where(
                        notDeleted(),
                        nameContains(filter.getName()),
                        institutionTypeEq(filter.getInstitutionType()),
                        isAdmissionAvailableEq(filter.getIsAdmissionAvailable()),
                        cityContains(filter.getCity()),
                        monthlyFeeLoe(filter.getMaxMonthlyFee())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(createOrderSpecifiers(pageable))
                .fetch();

        Long total = queryFactory
                .select(institution.count())
                .from(institution)
                .where(
                        notDeleted(),
                        nameContains(filter.getName()),
                        institutionTypeEq(filter.getInstitutionType()),
                        isAdmissionAvailableEq(filter.getIsAdmissionAvailable()),
                        cityContains(filter.getCity()),
                        monthlyFeeLoe(filter.getMaxMonthlyFee())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    // ==================== Private Methods ====================

    /**
     * WHERE 조건 빌더 (BooleanBuilder 방식)
     */
    private BooleanBuilder createWhereClause(InstitutionSearchFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();

        // 기본 조건: 삭제되지 않은 기관만
        builder.and(institution.deleted.eq(false));

        // 동적 조건 추가
        if (filter == null) {
            return builder;
        }

        if (filter.getName() != null && !filter.getName().isBlank()) {
            builder.and(institution.name.containsIgnoreCase(filter.getName()));
        }

        if (filter.getInstitutionType() != null) {
            builder.and(institution.institutionType.eq(filter.getInstitutionType()));
        }

        if (filter.getIsAdmissionAvailable() != null) {
            builder.and(institution.isAdmissionAvailable.eq(filter.getIsAdmissionAvailable()));
        }

        if (filter.getCity() != null && !filter.getCity().isBlank()) {
            builder.and(institution.address.city.containsIgnoreCase(filter.getCity()));
        }

        if (filter.getMaxMonthlyFee() != null) {
            builder.and(institution.priceInfo.monthlyBaseFee.loe(filter.getMaxMonthlyFee()));
        }

        return builder;
    }

    /**
     * 동적 정렬 적용
     */
    private void applySort(JPAQuery<Institution> query, Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            OrderSpecifier<?> orderSpecifier = createOrderSpecifier(order);
            if (orderSpecifier != null) {
                query.orderBy(orderSpecifier);
            }
        }
    }

    /**
     * 동적 정렬 조건 생성 (단일)
     */
    private OrderSpecifier<?> createOrderSpecifier(Sort.Order order) {
        String property = order.getProperty();
        boolean isAsc = order.isAscending();

        return switch (property) {
            case "name" -> isAsc ? institution.name.asc() : institution.name.desc();
            case "createdAt" -> isAsc ? institution.createdAt.asc() : institution.createdAt.desc();
            case "bedCount" -> isAsc ? institution.bedCount.asc() : institution.bedCount.desc();
            case "monthlyBaseFee" -> isAsc ?
                    institution.priceInfo.monthlyBaseFee.asc() :
                    institution.priceInfo.monthlyBaseFee.desc();
            case "id" -> isAsc ? institution.id.asc() : institution.id.desc();
            default -> {
                log.warn("지원하지 않는 정렬 필드: {}", property);
                yield null;
            }
        };
    }

    /**
     * 동적 정렬 조건 생성 (복수)
     */
    private OrderSpecifier<?>[] createOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            OrderSpecifier<?> orderSpecifier = createOrderSpecifier(order);
            if (orderSpecifier != null) {
                orderSpecifiers.add(orderSpecifier);
            }
        }

        // 기본 정렬: createdAt DESC, id DESC
        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(institution.createdAt.desc());
            orderSpecifiers.add(institution.id.desc());
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    // ==================== BooleanExpression 방식 (재사용 가능, 권장) ====================

    /**
     * 삭제되지 않은 기관
     */
    private BooleanExpression notDeleted() {
        return institution.deleted.eq(false);
    }

    /**
     * 기관명 검색 (부분 일치, 대소문자 무시)
     */
    private BooleanExpression nameContains(String name) {
        return (name != null && !name.isBlank()) ?
                institution.name.containsIgnoreCase(name) : null;
    }

    /**
     * 기관 유형 필터
     */
    private BooleanExpression institutionTypeEq(InstitutionType type) {
        return type != null ? institution.institutionType.eq(type) : null;
    }

    /**
     * 승인 상태 필터
     */
    private BooleanExpression approvalStatusEq(ApprovalStatus status) {
        return status != null ? institution.approvalStatus.eq(status) : null;
    }

    /**
     * 입소 가능 여부 필터
     */
    private BooleanExpression isAdmissionAvailableEq(Boolean isAdmissionAvailable) {
        return isAdmissionAvailable != null ?
                institution.isAdmissionAvailable.eq(isAdmissionAvailable) : null;
    }

    /**
     * 도시 검색 (부분 일치, 대소문자 무시)
     */
    private BooleanExpression cityContains(String city) {
        return (city != null && !city.isBlank()) ?
                institution.address.city.containsIgnoreCase(city) : null;
    }

    /**
     * 최소 병상 수
     */
    private BooleanExpression bedCountGoe(Integer minBedCount) {
        return minBedCount != null ? institution.bedCount.goe(minBedCount) : null;
    }

    /**
     * 최대 병상 수
     */
    private BooleanExpression bedCountLoe(Integer maxBedCount) {
        return maxBedCount != null ? institution.bedCount.loe(maxBedCount) : null;
    }

    /**
     * 최대 월 기본 요금
     */
    private BooleanExpression monthlyFeeLoe(Integer maxMonthlyFee) {
        return maxMonthlyFee != null ?
                institution.priceInfo.monthlyBaseFee.loe(maxMonthlyFee) : null;
    }
}
