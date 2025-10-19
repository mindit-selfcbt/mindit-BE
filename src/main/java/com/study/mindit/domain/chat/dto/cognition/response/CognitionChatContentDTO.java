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
public class CognitionChatContentDTO {
    
    @JsonProperty("encouragement_message")
    private String encouragementMessage;

    @JsonProperty("cognitive_error_explanation")
    private String cognitiveErrorExplanation;

    @JsonProperty("personalized_situation")
    private String personalizedSituation;

    @JsonProperty("emotion_question")
    private String emotionQuestion;
}
