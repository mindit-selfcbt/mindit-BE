package com.study.mindit.domain.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.mindit.domain.chat.domain.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO_1 {

    private String content;

    private String sessionId;

    public Chat toEntity() {
        return Chat.builder()
                .sessionId(sessionId)
                .content(content)
                .build();
    }
}
