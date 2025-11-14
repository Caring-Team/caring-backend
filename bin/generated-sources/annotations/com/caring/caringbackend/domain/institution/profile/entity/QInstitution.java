package com.caring.caringbackend.domain.institution.profile.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInstitution is a Querydsl query type for Institution
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInstitution extends EntityPathBase<Institution> {

    private static final long serialVersionUID = 863253335L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInstitution institution = new QInstitution("institution");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    public final com.caring.caringbackend.global.model.QAddress address;

    public final ListPath<InstitutionAdmin, QInstitutionAdmin> admins = this.<InstitutionAdmin, QInstitutionAdmin>createList("admins", InstitutionAdmin.class, QInstitutionAdmin.class, PathInits.DIRECT2);

    public final EnumPath<ApprovalStatus> approvalStatus = createEnum("approvalStatus", ApprovalStatus.class);

    public final NumberPath<Integer> bedCount = createNumber("bedCount", Integer.class);

    public final ListPath<CareGiver, QCareGiver> careGivers = this.<CareGiver, QCareGiver>createList("careGivers", CareGiver.class, QCareGiver.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<InstitutionType> institutionType = createEnum("institutionType", InstitutionType.class);

    public final BooleanPath isAdmissionAvailable = createBoolean("isAdmissionAvailable");

    public final com.caring.caringbackend.global.model.QGeoPoint location;

    public final StringPath name = createString("name");

    public final StringPath openingHours = createString("openingHours");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final QPriceInfo priceInfo;

    public final ListPath<InstitutionSpecializedCondition, QInstitutionSpecializedCondition> specializedConditions = this.<InstitutionSpecializedCondition, QInstitutionSpecializedCondition>createList("specializedConditions", InstitutionSpecializedCondition.class, QInstitutionSpecializedCondition.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QInstitution(String variable) {
        this(Institution.class, forVariable(variable), INITS);
    }

    public QInstitution(Path<? extends Institution> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInstitution(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInstitution(PathMetadata metadata, PathInits inits) {
        this(Institution.class, metadata, inits);
    }

    public QInstitution(Class<? extends Institution> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new com.caring.caringbackend.global.model.QAddress(forProperty("address")) : null;
        this.location = inits.isInitialized("location") ? new com.caring.caringbackend.global.model.QGeoPoint(forProperty("location")) : null;
        this.priceInfo = inits.isInitialized("priceInfo") ? new QPriceInfo(forProperty("priceInfo")) : null;
    }

}

