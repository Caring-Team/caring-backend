package com.caring.caringbackend.domain.auth.dto.request.user.oauth;

import com.caring.caringbackend.global.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserOAuth2RegisterRequest {


    @NotNull
    private Integer gender;

    @Valid
    @NotNull
    private Address address;
}
