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
public class CognitionChatResponseDTO_5 {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("intro_message")
    private String introMessage;

    @JsonProperty("cognitive_restructuring")
    private CognitiveRestructuringDTO cognitiveRestructuring;

    @JsonProperty("encouragement_message")
    private String encouragementMessage;

    @JsonProperty("insight_message")
    private String insightMessage;

    @JsonProperty("final_encouragement")
    private String finalEncouragement;

    @JsonProperty("choice_question")
    private String choiceQuestion;
}

