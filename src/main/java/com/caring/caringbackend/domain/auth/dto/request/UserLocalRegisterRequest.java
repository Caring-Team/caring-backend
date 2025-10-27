package com.caring.caringbackend.domain.auth.dto.request;

import com.caring.caringbackend.global.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserLocalRegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private Integer gender;

    @Valid
    @NotNull
    private Address address;
}
