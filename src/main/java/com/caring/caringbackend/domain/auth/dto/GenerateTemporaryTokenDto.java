package com.caring.caringbackend.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenerateTemporaryTokenDto {

    private String credentialType;
    private String credentialId;
}
