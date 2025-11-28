package com.caring.caringbackend.api.internal.institutionadmin.dto.response;

import com.caring.caringbackend.domain.institution.invitation.entity.InstitutionInvitation;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InstitutionAdminInvitationResponseDto {

    private Long id;

    @JsonProperty("institution_name")
    private String institutionName;

    @JsonProperty("requested_at")
    private LocalDate requestedAt;

    public static InstitutionAdminInvitationResponseDto from(InstitutionInvitation invitation) {
        return InstitutionAdminInvitationResponseDto.builder()
                .id(invitation.getId())
                .institutionName(invitation.getInstitution().getName())
                .requestedAt(invitation.getCreatedAt().toLocalDate())
                .build();
    }
}
