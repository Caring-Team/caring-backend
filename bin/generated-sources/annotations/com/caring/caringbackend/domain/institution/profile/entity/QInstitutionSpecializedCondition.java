package com.caring.caringbackend.domain.institution.profile.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInstitutionSpecializedCondition is a Querydsl query type for InstitutionSpecializedCondition
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInstitutionSpecializedCondition extends EntityPathBase<InstitutionSpecializedCondition> {

    private static final long serialVersionUID = 1301251913L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInstitutionSpecializedCondition institutionSpecializedCondition = new QInstitutionSpecializedCondition("institutionSpecializedCondition");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    public final EnumPath<SpecializedCondition> conditionType = createEnum("conditionType", SpecializedCondition.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInstitution institution;

    public final BooleanPath isActive = createBoolean("isActive");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QInstitutionSpecializedCondition(String variable) {
        this(InstitutionSpecializedCondition.class, forVariable(variable), INITS);
    }

    public QInstitutionSpecializedCondition(Path<? extends InstitutionSpecializedCondition> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInstitutionSpecializedCondition(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInstitutionSpecializedCondition(PathMetadata metadata, PathInits inits) {
        this(InstitutionSpecializedCondition.class, metadata, inits);
    }

    public QInstitutionSpecializedCondition(Class<? extends InstitutionSpecializedCondition> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institution = inits.isInitialized("institution") ? new QInstitution(forProperty("institution"), inits.get("institution")) : null;
    }

}

