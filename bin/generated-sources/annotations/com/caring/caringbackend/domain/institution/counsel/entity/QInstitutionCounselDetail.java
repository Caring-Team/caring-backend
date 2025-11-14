package com.caring.caringbackend.domain.institution.counsel.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInstitutionCounselDetail is a Querydsl query type for InstitutionCounselDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInstitutionCounselDetail extends EntityPathBase<InstitutionCounselDetail> {

    private static final long serialVersionUID = -1427892901L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInstitutionCounselDetail institutionCounselDetail = new QInstitutionCounselDetail("institutionCounselDetail");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInstitutionCounsel institutionCounsel;

    public final DatePath<java.time.LocalDate> serviceDate = createDate("serviceDate", java.time.LocalDate.class);

    public final NumberPath<Long> timeSlotsBitmask = createNumber("timeSlotsBitmask", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QInstitutionCounselDetail(String variable) {
        this(InstitutionCounselDetail.class, forVariable(variable), INITS);
    }

    public QInstitutionCounselDetail(Path<? extends InstitutionCounselDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInstitutionCounselDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInstitutionCounselDetail(PathMetadata metadata, PathInits inits) {
        this(InstitutionCounselDetail.class, metadata, inits);
    }

    public QInstitutionCounselDetail(Class<? extends InstitutionCounselDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institutionCounsel = inits.isInitialized("institutionCounsel") ? new QInstitutionCounsel(forProperty("institutionCounsel"), inits.get("institutionCounsel")) : null;
    }

}

