package com.caring.caringbackend.domain.institution.advertisement.repository;

import com.caring.caringbackend.api.institution.dto.response.advertisement.AdvertisementResponseDto;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisement;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.caring.caringbackend.domain.institution.advertisement.entity.QInstitutionAdvertisement.institutionAdvertisement;

@RequiredArgsConstructor
public class InstitutionAdvertisementRepositoryImpl implements InstitutionAdvertisementCustom {

    private final JPAQueryFactory jpaQueryFactory;
    /**
     * 관리자용 광고 목록 필터링 (페이징 있음)
     * */
    @Override
    public Page<InstitutionAdvertisement> findAllWithFilters(
            AdvertisementStatus status,
            AdvertisementType type,
            Pageable pageable
    ) {
        List<InstitutionAdvertisement> content = jpaQueryFactory
                .selectFrom(institutionAdvertisement)
                .where(
                        type != null ? institutionAdvertisement.type.eq(type) : null,
                        status != null ? institutionAdvertisement.status.eq(status) : null
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(institutionAdvertisement.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(institutionAdvertisement.count())
                .from(institutionAdvertisement)
                .where(
                        type != null ? institutionAdvertisement.type.eq(type) : null,
                        status != null ? institutionAdvertisement.status.eq(status) : null
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
