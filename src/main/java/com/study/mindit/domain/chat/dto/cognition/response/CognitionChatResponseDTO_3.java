package com.study.mindit.domain.chat.dto.cognition.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CognitionChatResponseDTO_3 {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("empathy_message")
    private String empathyMessage;

    @JsonProperty("insight_message")
    private String insightMessage;

    @JsonProperty("encouragement_message")
    private String encouragementMessage;

    @JsonProperty("choice_question")
    private String choiceQuestion;
}

