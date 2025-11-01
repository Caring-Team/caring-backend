package com.caring.caringbackend.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class RefreshTokenPayloadDto {

    private final Long id;
}
