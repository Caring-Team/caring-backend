package com.caring.caringbackend.api.chat.dto.response;

import com.caring.caringbackend.domain.institution.counsel.entity.ConsultRequest;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.ConsultRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 상담 내역 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultRequestListResponse {

    private List<ConsultRequestItem> consultRequests;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
    private boolean first;
    private boolean last;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsultRequestItem {
        private Long id;
        private Long chatRoomId;
        private InstitutionInfo institution;
        private CounselInfo counsel;
        private ConsultRequestStatus status;
        private String lastMessageContent;
        private LocalDateTime lastMessageAt;
        private LocalDateTime createdAt;
        private LocalDateTime closedAt;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class InstitutionInfo {
            private Long id;
            private String name;
        }

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CounselInfo {
            private Long id;
            private String title;
        }
    }

    public static ConsultRequestListResponse of(Page<ConsultRequest> page, List<ConsultRequestItem> items) {
        return ConsultRequestListResponse.builder()
                .consultRequests(items)
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}

