package com.caring.caringbackend.api.auth.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class RefreshTokenPayloadDto {

    private final Long id;
}
