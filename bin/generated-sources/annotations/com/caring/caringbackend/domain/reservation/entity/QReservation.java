package com.caring.caringbackend.domain.reservation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReservation is a Querydsl query type for Reservation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReservation extends EntityPathBase<Reservation> {

    private static final long serialVersionUID = 1353775986L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReservation reservation = new QReservation("reservation");

    public final com.caring.caringbackend.global.model.QBaseTimeEntity _super = new com.caring.caringbackend.global.model.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final com.caring.caringbackend.domain.user.elderly.entity.QElderlyProfile elderlyProfile;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.caring.caringbackend.domain.institution.counsel.entity.QInstitutionCounsel institutionCounsel;

    public final com.caring.caringbackend.domain.user.guardian.entity.QMember member;

    public final DatePath<java.time.LocalDate> reservationDate = createDate("reservationDate", java.time.LocalDate.class);

    public final StringPath reservationTime = createString("reservationTime");

    public final EnumPath<ReservationStatus> status = createEnum("status", ReservationStatus.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QReservation(String variable) {
        this(Reservation.class, forVariable(variable), INITS);
    }

    public QReservation(Path<? extends Reservation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReservation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReservation(PathMetadata metadata, PathInits inits) {
        this(Reservation.class, metadata, inits);
    }

    public QReservation(Class<? extends Reservation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.elderlyProfile = inits.isInitialized("elderlyProfile") ? new com.caring.caringbackend.domain.user.elderly.entity.QElderlyProfile(forProperty("elderlyProfile"), inits.get("elderlyProfile")) : null;
        this.institutionCounsel = inits.isInitialized("institutionCounsel") ? new com.caring.caringbackend.domain.institution.counsel.entity.QInstitutionCounsel(forProperty("institutionCounsel"), inits.get("institutionCounsel")) : null;
        this.member = inits.isInitialized("member") ? new com.caring.caringbackend.domain.user.guardian.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

