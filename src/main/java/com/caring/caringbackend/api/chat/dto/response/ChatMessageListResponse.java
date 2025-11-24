package com.caring.caringbackend.api.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 💬 채팅 메시지 목록 응답 DTO
 * <p>
 * 메시지 목록 조회 시 페이징 정보와 함께 반환하는 응답 객체입니다.
 *
 * @author 윤다인
 * @since 2025-11-23
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageListResponse {

    /**
     * 💬 메시지 목록
     */
    private List<ChatMessageResponse> messages;

    /**
     * 📄 페이징 정보
     */
    private PageInfo pageInfo;

    /**
     * 📄 페이징 정보 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private int size;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    /**
     * 📤 메시지 목록과 Page 정보로 ChatMessageListResponse 생성
     *
     * @param messages 메시지 응답 목록
     * @param page 페이지 정보
     * @return ChatMessageListResponse
     */
    public static ChatMessageListResponse of(List<ChatMessageResponse> messages, Page<?> page) {
        return ChatMessageListResponse.builder()
                .messages(messages)
                .pageInfo(PageInfo.builder()
                        .currentPage(page.getNumber())
                        .totalPages(page.getTotalPages())
                        .totalElements(page.getTotalElements())
                        .size(page.getSize())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .build();
    }
}

