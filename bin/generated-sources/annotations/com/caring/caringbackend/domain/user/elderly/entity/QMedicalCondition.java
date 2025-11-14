package com.caring.caringbackend.domain.user.elderly.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMedicalCondition is a Querydsl query type for MedicalCondition
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMedicalCondition extends EntityPathBase<MedicalCondition> {

    private static final long serialVersionUID = -248474870L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMedicalCondition medicalCondition = new QMedicalCondition("medicalCondition");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    public final StringPath conditionName = createString("conditionName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath description = createString("description");

    public final DatePath<java.time.LocalDate> diagnosed_at = createDate("diagnosed_at", java.time.LocalDate.class);

    public final QElderlyProfile elderlyProfile;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMedicalCondition(String variable) {
        this(MedicalCondition.class, forVariable(variable), INITS);
    }

    public QMedicalCondition(Path<? extends MedicalCondition> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMedicalCondition(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMedicalCondition(PathMetadata metadata, PathInits inits) {
        this(MedicalCondition.class, metadata, inits);
    }

    public QMedicalCondition(Class<? extends MedicalCondition> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.elderlyProfile = inits.isInitialized("elderlyProfile") ? new QElderlyProfile(forProperty("elderlyProfile"), inits.get("elderlyProfile")) : null;
    }

}

