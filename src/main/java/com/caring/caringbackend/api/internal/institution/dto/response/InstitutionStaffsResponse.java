package com.caring.caringbackend.api.internal.institution.dto.response;

import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class InstitutionStaffsResponse {

    private List<InstitutionStaffResponse> staffs;

    public static InstitutionStaffsResponse from(List<InstitutionAdmin> admins) {
        return InstitutionStaffsResponse.builder()
                .staffs(admins.stream().map(InstitutionStaffResponse::from).toList())
                .build();
    }

    @Data
    @Builder
    private static class InstitutionStaffResponse {

        private Long id;

        private String name;

        private String username;

        private LocalDateTime lastActivityAt;

        public static InstitutionStaffResponse from(InstitutionAdmin admin) {
            return InstitutionStaffResponse.builder()
                    .id(admin.getId())
                    .name(admin.getName())
                    .username(admin.getUsername())
                    .lastActivityAt(admin.getLastActivityAt())
                    .build();
        }
    }
}
