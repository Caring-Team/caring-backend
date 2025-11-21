package com.caring.caringbackend.domain.institution.profile.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInstitutionAdmin is a Querydsl query type for InstitutionAdmin
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInstitutionAdmin extends EntityPathBase<InstitutionAdmin> {

    private static final long serialVersionUID = 1953259064L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInstitutionAdmin institutionAdmin = new QInstitutionAdmin("institutionAdmin");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath duplicationInformation = createString("duplicationInformation");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInstitution institution;

    public final StringPath name = createString("name");

    public final StringPath passwordHash = createString("passwordHash");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final EnumPath<InstitutionAdminRole> role = createEnum("role", InstitutionAdminRole.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath username = createString("username");

    public QInstitutionAdmin(String variable) {
        this(InstitutionAdmin.class, forVariable(variable), INITS);
    }

    public QInstitutionAdmin(Path<? extends InstitutionAdmin> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInstitutionAdmin(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInstitutionAdmin(PathMetadata metadata, PathInits inits) {
        this(InstitutionAdmin.class, metadata, inits);
    }

    public QInstitutionAdmin(Class<? extends InstitutionAdmin> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institution = inits.isInitialized("institution") ? new QInstitution(forProperty("institution"), inits.get("institution")) : null;
    }

}

