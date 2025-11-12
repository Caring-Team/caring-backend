package com.caring.caringbackend.domain.tag.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewTagMapping is a Querydsl query type for ReviewTagMapping
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewTagMapping extends EntityPathBase<ReviewTagMapping> {

    private static final long serialVersionUID = -1188157388L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewTagMapping reviewTagMapping = new QReviewTagMapping("reviewTagMapping");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.caring.caringbackend.domain.review.entity.QReview review;

    public final QTag tag;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QReviewTagMapping(String variable) {
        this(ReviewTagMapping.class, forVariable(variable), INITS);
    }

    public QReviewTagMapping(Path<? extends ReviewTagMapping> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewTagMapping(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewTagMapping(PathMetadata metadata, PathInits inits) {
        this(ReviewTagMapping.class, metadata, inits);
    }

    public QReviewTagMapping(Class<? extends ReviewTagMapping> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.review = inits.isInitialized("review") ? new com.caring.caringbackend.domain.review.entity.QReview(forProperty("review"), inits.get("review")) : null;
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
    }

}

