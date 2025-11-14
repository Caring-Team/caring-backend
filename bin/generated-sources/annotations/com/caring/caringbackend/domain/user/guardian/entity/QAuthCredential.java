package com.caring.caringbackend.domain.user.guardian.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuthCredential is a Querydsl query type for AuthCredential
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthCredential extends EntityPathBase<AuthCredential> {

    private static final long serialVersionUID = 752509185L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuthCredential authCredential = new QAuthCredential("authCredential");

    public final com.caring.caringbackend.global.model.QBaseTimeEntity _super = new com.caring.caringbackend.global.model.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath identifier = createString("identifier");

    public final QMember member;

    public final StringPath passwordHash = createString("passwordHash");

    public final EnumPath<CredentialType> type = createEnum("type", CredentialType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QAuthCredential(String variable) {
        this(AuthCredential.class, forVariable(variable), INITS);
    }

    public QAuthCredential(Path<? extends AuthCredential> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuthCredential(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuthCredential(PathMetadata metadata, PathInits inits) {
        this(AuthCredential.class, metadata, inits);
    }

    public QAuthCredential(Class<? extends AuthCredential> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

