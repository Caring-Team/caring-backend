package com.caring.caringbackend.domain.institution.counsel.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInstitutionCounsel is a Querydsl query type for InstitutionCounsel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInstitutionCounsel extends EntityPathBase<InstitutionCounsel> {

    private static final long serialVersionUID = -1496938390L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInstitutionCounsel institutionCounsel = new QInstitutionCounsel("institutionCounsel");

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

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QInstitutionCounsel(String variable) {
        this(InstitutionCounsel.class, forVariable(variable), INITS);
    }

    public QInstitutionCounsel(Path<? extends InstitutionCounsel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInstitutionCounsel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInstitutionCounsel(PathMetadata metadata, PathInits inits) {
        this(InstitutionCounsel.class, metadata, inits);
    }

    public QInstitutionCounsel(Class<? extends InstitutionCounsel> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institution = inits.isInitialized("institution") ? new com.caring.caringbackend.domain.institution.profile.entity.QInstitution(forProperty("institution"), inits.get("institution")) : null;
    }

}

