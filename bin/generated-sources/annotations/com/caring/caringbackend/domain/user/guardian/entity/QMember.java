package com.caring.caringbackend.domain.user.guardian.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 2107391356L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    public final com.caring.caringbackend.global.model.QAddress address;

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath duplicationInformation = createString("duplicationInformation");

    public final ListPath<com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile, com.caring.caringbackend.domain.user.elderly.entity.QElderlyProfile> elderlyProfiles = this.<com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile, com.caring.caringbackend.domain.user.elderly.entity.QElderlyProfile>createList("elderlyProfiles", com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile.class, com.caring.caringbackend.domain.user.elderly.entity.QElderlyProfile.class, PathInits.DIRECT2);

    public final EnumPath<com.caring.caringbackend.global.model.Gender> gender = createEnum("gender", com.caring.caringbackend.global.model.Gender.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.caring.caringbackend.global.model.QGeoPoint location;

    public final StringPath name = createString("name");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final EnumPath<MemberRole> role = createEnum("role", MemberRole.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new com.caring.caringbackend.global.model.QAddress(forProperty("address")) : null;
        this.location = inits.isInitialized("location") ? new com.caring.caringbackend.global.model.QGeoPoint(forProperty("location")) : null;
    }

}

