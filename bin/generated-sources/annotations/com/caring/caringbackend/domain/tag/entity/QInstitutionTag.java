package com.caring.caringbackend.domain.tag.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInstitutionTag is a Querydsl query type for InstitutionTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInstitutionTag extends EntityPathBase<InstitutionTag> {

    private static final long serialVersionUID = -659926102L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInstitutionTag institutionTag = new QInstitutionTag("institutionTag");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    public final StringPath additionalInfo = createString("additionalInfo");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.caring.caringbackend.domain.institution.profile.entity.QInstitution institution;

    public final QTag tag;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QInstitutionTag(String variable) {
        this(InstitutionTag.class, forVariable(variable), INITS);
    }

    public QInstitutionTag(Path<? extends InstitutionTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInstitutionTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInstitutionTag(PathMetadata metadata, PathInits inits) {
        this(InstitutionTag.class, metadata, inits);
    }

    public QInstitutionTag(Class<? extends InstitutionTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institution = inits.isInitialized("institution") ? new com.caring.caringbackend.domain.institution.profile.entity.QInstitution(forProperty("institution"), inits.get("institution")) : null;
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
    }

}

