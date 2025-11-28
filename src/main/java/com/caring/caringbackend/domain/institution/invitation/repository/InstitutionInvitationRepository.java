package com.caring.caringbackend.domain.institution.invitation.repository;

import com.caring.caringbackend.domain.institution.invitation.entity.InstitutionInvitation;
import com.caring.caringbackend.domain.institution.invitation.entity.enums.InstitutionInvitationStatus;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionInvitationRepository extends JpaRepository<InstitutionInvitation, Long> {

    boolean existsByInstitutionAndInviteeAndStatus(
            Institution institution, InstitutionAdmin invitee, InstitutionInvitationStatus status);


    List<InstitutionInvitation> findAllByInvitee(InstitutionAdmin invitee);
    List<InstitutionInvitation> findAllByInviteeAndStatus(InstitutionAdmin invitee, InstitutionInvitationStatus status);
    List<InstitutionInvitation> findAllByInstitution(Institution institution);
}
