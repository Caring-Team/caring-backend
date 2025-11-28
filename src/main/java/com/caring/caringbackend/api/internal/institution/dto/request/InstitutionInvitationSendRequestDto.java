package com.caring.caringbackend.api.internal.institution.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InstitutionInvitationSendRequestDto {

    @NotBlank
    private String username;
}
