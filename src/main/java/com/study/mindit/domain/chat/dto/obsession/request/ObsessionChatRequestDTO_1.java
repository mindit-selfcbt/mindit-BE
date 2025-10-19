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
public class ObsessionChatRequestDTO_1 {

    @JsonProperty("user_text")
    private Object content;

    @JsonProperty("session_id")
    private String sessionId;
}
