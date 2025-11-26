package com.caring.caringbackend.api.institution.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDto {

    private Long pending;
    @JsonProperty("confirm_today")
    private Long confirmToday;
    @JsonProperty("cancel_today")
    private Long cancelToday;
    @JsonProperty("recent_review")
    private Long recentReview;
}
