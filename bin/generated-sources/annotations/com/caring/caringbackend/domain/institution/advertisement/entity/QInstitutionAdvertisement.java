package com.caring.caringbackend.domain.institution.advertisement.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInstitutionAdvertisement is a Querydsl query type for InstitutionAdvertisement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInstitutionAdvertisement extends EntityPathBase<InstitutionAdvertisement> {

    private static final long serialVersionUID = -1242237942L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInstitutionAdvertisement institutionAdvertisement = new QInstitutionAdvertisement("institutionAdvertisement");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final DateTimePath<java.time.LocalDateTime> endDateTime = createDateTime("endDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.caring.caringbackend.domain.institution.profile.entity.QInstitution institution;

    public final DateTimePath<java.time.LocalDateTime> startDateTime = createDateTime("startDateTime", java.time.LocalDateTime.class);

    public final EnumPath<AdvertisementStatus> status = createEnum("status", AdvertisementStatus.class);

    public final EnumPath<AdvertisementType> type = createEnum("type", AdvertisementType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QInstitutionAdvertisement(String variable) {
        this(InstitutionAdvertisement.class, forVariable(variable), INITS);
    }

    public QInstitutionAdvertisement(Path<? extends InstitutionAdvertisement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInstitutionAdvertisement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInstitutionAdvertisement(PathMetadata metadata, PathInits inits) {
        this(InstitutionAdvertisement.class, metadata, inits);
    }

    public QInstitutionAdvertisement(Class<? extends InstitutionAdvertisement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institution = inits.isInitialized("institution") ? new com.caring.caringbackend.domain.institution.profile.entity.QInstitution(forProperty("institution"), inits.get("institution")) : null;
    }

}

