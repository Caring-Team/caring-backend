package com.caring.caringbackend.domain.institution.invitation.entity;

import com.caring.caringbackend.domain.institution.invitation.entity.enums.InstitutionInvitationStatus;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InstitutionInvitation extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invitee_id", nullable = false)
    InstitutionAdmin invitee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institution_id", nullable = false)
    Institution institution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    InstitutionInvitationStatus status = InstitutionInvitationStatus.PENDING;

    @Builder
    public InstitutionInvitation(InstitutionAdmin invitee, Institution institution) {
        this.invitee = invitee;
        this.institution = institution;
    }

    public void accept() {
        ensurePending();
        setStatus(InstitutionInvitationStatus.ACCEPTED);
    }

    public void reject() {
        ensurePending();
        setStatus(InstitutionInvitationStatus.REJECTED);
    }

    public void cancel() {
        ensurePending();
        setStatus(InstitutionInvitationStatus.CANCELED);
    }

    public boolean isPending() {
        return this.status == InstitutionInvitationStatus.PENDING;
    }

    private void ensurePending() {
        if (!isPending()) {
            throw new BusinessException(ErrorCode.INSTITUTION_INVITATION_ALREADY_SOLVED);
        }
    }

    private void setStatus(InstitutionInvitationStatus status) {
        this.status = status;
    }
}
