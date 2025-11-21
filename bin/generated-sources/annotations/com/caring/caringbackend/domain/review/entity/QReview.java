package com.caring.caringbackend.domain.review.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = 1488897660L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReview review = new QReview("review");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.caring.caringbackend.domain.institution.profile.entity.QInstitution institution;

    public final com.caring.caringbackend.domain.user.guardian.entity.QMember member;

    public final NumberPath<Integer> rating = createNumber("rating", Integer.class);

    public final BooleanPath reported = createBoolean("reported");

    public final com.caring.caringbackend.domain.reservation.entity.QReservation reservation;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QReview(String variable) {
        this(Review.class, forVariable(variable), INITS);
    }

    public QReview(Path<? extends Review> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReview(PathMetadata metadata, PathInits inits) {
        this(Review.class, metadata, inits);
    }

    public QReview(Class<? extends Review> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institution = inits.isInitialized("institution") ? new com.caring.caringbackend.domain.institution.profile.entity.QInstitution(forProperty("institution"), inits.get("institution")) : null;
        this.member = inits.isInitialized("member") ? new com.caring.caringbackend.domain.user.guardian.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.reservation = inits.isInitialized("reservation") ? new com.caring.caringbackend.domain.reservation.entity.QReservation(forProperty("reservation"), inits.get("reservation")) : null;
    }

}

