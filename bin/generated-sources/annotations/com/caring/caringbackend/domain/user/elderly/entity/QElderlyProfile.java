package com.caring.caringbackend.domain.user.elderly.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QElderlyProfile is a Querydsl query type for ElderlyProfile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QElderlyProfile extends EntityPathBase<ElderlyProfile> {

    private static final long serialVersionUID = 1022763538L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QElderlyProfile elderlyProfile = new QElderlyProfile("elderlyProfile");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    public final EnumPath<ActivityLevel> activityLevel = createEnum("activityLevel", ActivityLevel.class);

    public final com.caring.caringbackend.global.model.QAddress address;

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    public final EnumPath<BloodType> bloodType = createEnum("bloodType", BloodType.class);

    public final EnumPath<CognitiveLevel> cognitiveLevel = createEnum("cognitiveLevel", CognitiveLevel.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final EnumPath<com.caring.caringbackend.global.model.Gender> gender = createEnum("gender", com.caring.caringbackend.global.model.Gender.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.caring.caringbackend.global.model.QGeoPoint location;

    public final EnumPath<LongTermCareGrade> longTermCareGrade = createEnum("longTermCareGrade", LongTermCareGrade.class);

    public final com.caring.caringbackend.domain.user.guardian.entity.QMember member;

    public final StringPath name = createString("name");

    public final StringPath notes = createString("notes");

    public final StringPath phoneNumber = createString("phoneNumber");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QElderlyProfile(String variable) {
        this(ElderlyProfile.class, forVariable(variable), INITS);
    }

    public QElderlyProfile(Path<? extends ElderlyProfile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QElderlyProfile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QElderlyProfile(PathMetadata metadata, PathInits inits) {
        this(ElderlyProfile.class, metadata, inits);
    }

    public QElderlyProfile(Class<? extends ElderlyProfile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new com.caring.caringbackend.global.model.QAddress(forProperty("address")) : null;
        this.location = inits.isInitialized("location") ? new com.caring.caringbackend.global.model.QGeoPoint(forProperty("location")) : null;
        this.member = inits.isInitialized("member") ? new com.caring.caringbackend.domain.user.guardian.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

