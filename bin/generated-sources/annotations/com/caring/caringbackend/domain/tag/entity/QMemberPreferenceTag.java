package com.caring.caringbackend.domain.tag.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberPreferenceTag is a Querydsl query type for MemberPreferenceTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberPreferenceTag extends EntityPathBase<MemberPreferenceTag> {

    private static final long serialVersionUID = -1866896291L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberPreferenceTag memberPreferenceTag = new QMemberPreferenceTag("memberPreferenceTag");

    public final com.caring.caringbackend.global.model.QBaseEntity _super = new com.caring.caringbackend.global.model.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.caring.caringbackend.domain.user.guardian.entity.QMember member;

    public final QTag tag;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMemberPreferenceTag(String variable) {
        this(MemberPreferenceTag.class, forVariable(variable), INITS);
    }

    public QMemberPreferenceTag(Path<? extends MemberPreferenceTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberPreferenceTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberPreferenceTag(PathMetadata metadata, PathInits inits) {
        this(MemberPreferenceTag.class, metadata, inits);
    }

    public QMemberPreferenceTag(Class<? extends MemberPreferenceTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.caring.caringbackend.domain.user.guardian.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
    }

}

