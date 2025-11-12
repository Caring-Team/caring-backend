package com.caring.caringbackend.domain.institution.advertisement.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInstitutionAdvertisementRequest is a Querydsl query type for InstitutionAdvertisementRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInstitutionAdvertisementRequest extends EntityPathBase<InstitutionAdvertisementRequest> {

    private static final long serialVersionUID = -1046227163L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInstitutionAdvertisementRequest institutionAdvertisementRequest = new QInstitutionAdvertisementRequest("institutionAdvertisementRequest");

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

    public QInstitutionAdvertisementRequest(String variable) {
        this(InstitutionAdvertisementRequest.class, forVariable(variable), INITS);
    }

    public QInstitutionAdvertisementRequest(Path<? extends InstitutionAdvertisementRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInstitutionAdvertisementRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInstitutionAdvertisementRequest(PathMetadata metadata, PathInits inits) {
        this(InstitutionAdvertisementRequest.class, metadata, inits);
    }

    public QInstitutionAdvertisementRequest(Class<? extends InstitutionAdvertisementRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institution = inits.isInitialized("institution") ? new com.caring.caringbackend.domain.institution.profile.entity.QInstitution(forProperty("institution"), inits.get("institution")) : null;
    }

}

