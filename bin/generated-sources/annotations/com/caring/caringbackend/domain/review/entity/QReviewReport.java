package com.caring.caringbackend.domain.review.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewReport is a Querydsl query type for ReviewReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewReport extends EntityPathBase<ReviewReport> {

    private static final long serialVersionUID = -1790428464L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewReport reviewReport = new QReviewReport("reviewReport");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.caring.caringbackend.domain.institution.profile.entity.QInstitution institution;

    public final com.caring.caringbackend.domain.user.guardian.entity.QMember member;

    public final EnumPath<ReportReason> reason = createEnum("reason", ReportReason.class);

    public final QReview review;

    public final EnumPath<ReportStatus> status = createEnum("status", ReportStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QReviewReport(String variable) {
        this(ReviewReport.class, forVariable(variable), INITS);
    }

    public QReviewReport(Path<? extends ReviewReport> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewReport(PathMetadata metadata, PathInits inits) {
        this(ReviewReport.class, metadata, inits);
    }

    public QReviewReport(Class<? extends ReviewReport> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institution = inits.isInitialized("institution") ? new com.caring.caringbackend.domain.institution.profile.entity.QInstitution(forProperty("institution"), inits.get("institution")) : null;
        this.member = inits.isInitialized("member") ? new com.caring.caringbackend.domain.user.guardian.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.review = inits.isInitialized("review") ? new QReview(forProperty("review"), inits.get("review")) : null;
    }

}

