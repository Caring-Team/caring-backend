package com.caring.caringbackend.domain.institution.invitation.service;

import com.caring.caringbackend.api.institution.dto.response.InstitutionInvitationResponseDto;
import com.caring.caringbackend.api.institutionadmin.dto.response.InstitutionAdminInvitationResponseDto;
import com.caring.caringbackend.domain.institution.invitation.entity.InstitutionInvitation;
import com.caring.caringbackend.domain.institution.invitation.entity.enums.InstitutionInvitationStatus;
import com.caring.caringbackend.domain.institution.invitation.repository.InstitutionInvitationRepository;
import com.caring.caringbackend.domain.institution.profile.entity.ApprovalStatus;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class InstitutionInvitationService {

    private final InstitutionInvitationRepository institutionInvitationRepository;
    private final InstitutionAdminRepository institutionAdminRepository;

    @Transactional(readOnly = true)
    public List<InstitutionAdminInvitationResponseDto> getInstitutionAdminInvitations(Long adminId) {
        InstitutionAdmin invitee = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        validateInvitee(invitee);

        List<InstitutionInvitation> invitations = institutionInvitationRepository.findAllByInvitee(invitee);

        return invitations.stream().map(InstitutionAdminInvitationResponseDto::from).toList();
    }

    @Transactional
    public void acceptInstitutionInvitation(Long adminId, Long invitationId) {
        InstitutionAdmin invitee = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
        validateInvitee(invitee);

        InstitutionInvitation invitation = institutionInvitationRepository
                .findById(invitationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_INVITATION_NOT_FOUND));

        validateInvitation(invitation, invitee);

        Institution institution = invitation.getInstitution();

        validateInstitutionApproved(institution);

        invitee.linkInstitution(institution);
        invitation.accept();
    }

    @Transactional
    public void rejectInstitutionInvitation(Long adminId, Long invitationId) {
        InstitutionAdmin invitee = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
        validateInvitee(invitee);

        InstitutionInvitation invitation = institutionInvitationRepository
                .findById(invitationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_INVITATION_NOT_FOUND));

        validateInvitation(invitation, invitee);

        invitation.reject();
    }

    // INSTITUTION METHOD

    @Transactional(readOnly = true)
    public List<InstitutionInvitationResponseDto> getInstitutionInvitations(Long adminId) {
        InstitutionAdmin admin = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        Institution institution = Optional.of(admin.getInstitution())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION));

        validateInstitutionApproved(institution);

        List<InstitutionInvitation> invitations = institutionInvitationRepository.findAllByInstitution(institution);

        return invitations.stream().map(InstitutionInvitationResponseDto::from).toList();
    }

    @Transactional
    public void sendInstitutionInvitation(Long adminId, String inviteeUsername) {
        InstitutionAdmin invitee = institutionAdminRepository.findByUsername(inviteeUsername)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_INVITATION_USERNAME_NOT_FOUND));

        validateInvitee(invitee);

        InstitutionAdmin institutionAdmin = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        Institution institution = Optional.of(institutionAdmin.getInstitution())
                .orElseThrow((() -> new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION)));

        validateInstitutionApproved(institution);

        validateInvite(institution, invitee);

        InstitutionInvitation invitation = InstitutionInvitation.builder()
                .invitee(invitee)
                .institution(institution)
                .build();

        institutionInvitationRepository.save(invitation);
    }


    @Transactional
    public void cancelInstitutionInvitation(Long adminId, Long invitationId) {
        InstitutionInvitation invitation = institutionInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_INVITATION_NOT_FOUND));

        validateInvitation(invitation);

        InstitutionAdmin admin = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        Institution institution = Optional.of(admin.getInstitution())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION));

        validateInstitutionApproved(institution);

        if (!invitation.getInstitution().equals(institution)) {
            throw new BusinessException(ErrorCode.INSTITUTION_INVITATION_FORBIDDEN);
        }

        invitation.cancel();
    }

    private void validateInvitation(InstitutionInvitation invitation) {
        if (!invitation.isPending()) {
            throw new BusinessException(ErrorCode.INSTITUTION_INVITATION_ALREADY_SOLVED);
        }
    }

    private void validateInvitation(InstitutionInvitation invitation, InstitutionAdmin invitee) {
        validateInvitation(invitation);

        if (!invitation.getInvitee().equals(invitee)) {
            throw new BusinessException(ErrorCode.INSTITUTION_INVITATION_FORBIDDEN);
        }
    }

    private void validateInstitutionApproved(Institution institution) {
        if (institution.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BusinessException(ErrorCode.INSTITUTION_APPROVAL_PENDING);
        }
    }

    private void validateInvitee(InstitutionAdmin invitee) {
        if (invitee.hasInstitution()) {
            throw new BusinessException(ErrorCode.INSTITUTION_ALREADY_EXISTS);
        }
    }

    private void validateInvite(Institution institution, InstitutionAdmin invitee) {
        boolean hasActiveInvitation = institutionInvitationRepository
                .existsByInstitutionAndInviteeAndStatus(institution, invitee, InstitutionInvitationStatus.PENDING);

        if (hasActiveInvitation) {
            throw new BusinessException(ErrorCode.INSTITUTION_ALREADY_EXISTS);
        }
    }
}
