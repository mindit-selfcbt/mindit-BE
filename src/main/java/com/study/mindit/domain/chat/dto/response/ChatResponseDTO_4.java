package com.study.mindit.domain.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO_4 {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("user_pattern_summary")
    private String userPatternSummary;

    @JsonProperty("category_message")
    private String categoryMessage;

    @JsonProperty("encouragement")
    private String encouragement;
}


