package com.caring.caringbackend.api.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstitutionAdminMeResponse {

    @JsonProperty("user_name")
    private String name;
    @JsonProperty("user_role")
    private String role;
    @JsonProperty("institution_id")
    private Long institutionId;
    @JsonProperty("institution_name")
    private String institutionName;
}
