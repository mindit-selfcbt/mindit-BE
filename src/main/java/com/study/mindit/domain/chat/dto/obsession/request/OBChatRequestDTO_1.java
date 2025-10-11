package com.study.mindit.domain.chat.dto.obsession.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OBChatRequestDTO_1 {

    @JsonProperty("user_text")
    private Object content;  // String 또는 구조화된 객체 모두 허용

    @JsonProperty("session_id")
    private String sessionId;
}
