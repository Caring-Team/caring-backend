package com.caring.caringbackend.api.institution.dto.response;

import com.caring.caringbackend.domain.institution.invitation.entity.InstitutionInvitation;
import com.caring.caringbackend.domain.institution.invitation.entity.enums.InstitutionInvitationStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InstitutionInvitationResponseDto {

    private Long id;

    @JsonProperty("invitee_username")
    private String inviteeUsername;

    @JsonProperty("requested_at")
    private LocalDate requestedAt;

    private InstitutionInvitationStatus status;


    public static InstitutionInvitationResponseDto from(InstitutionInvitation invitation) {
        return InstitutionInvitationResponseDto.builder()
                .id(invitation.getId())
                .inviteeUsername(invitation.getInvitee().getUsername())
                .requestedAt(invitation.getCreatedAt().toLocalDate())
                .status(invitation.getStatus())
                .build();
    }
}
