package com.caring.caringbackend.domain.institution.profile.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCareGiver is a Querydsl query type for CareGiver
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCareGiver extends EntityPathBase<CareGiver> {

    private static final long serialVersionUID = 306329679L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCareGiver careGiver = new QCareGiver("careGiver");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath email = createString("email");

    public final StringPath experienceDetails = createString("experienceDetails");

    public final EnumPath<com.caring.caringbackend.global.model.Gender> gender = createEnum("gender", com.caring.caringbackend.global.model.Gender.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInstitution institution;

    public final StringPath name = createString("name");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath photoUrl = createString("photoUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QCareGiver(String variable) {
        this(CareGiver.class, forVariable(variable), INITS);
    }

    public QCareGiver(Path<? extends CareGiver> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCareGiver(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCareGiver(PathMetadata metadata, PathInits inits) {
        this(CareGiver.class, metadata, inits);
    }

    public QCareGiver(Class<? extends CareGiver> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institution = inits.isInitialized("institution") ? new QInstitution(forProperty("institution"), inits.get("institution")) : null;
    }

}

