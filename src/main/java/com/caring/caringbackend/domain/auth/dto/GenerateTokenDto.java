package com.caring.caringbackend.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenerateTokenDto {

    private Long id;

    private String role;
}
