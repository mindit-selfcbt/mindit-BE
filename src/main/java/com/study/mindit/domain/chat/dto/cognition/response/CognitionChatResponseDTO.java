package com.study.mindit.domain.chat.dto.cognition.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({
    "session_id",
    "obsession_type",
    "encouragement_message",
    "cognitive_error_explanation",
    "personalized_situation",
    "emotion_question"
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CognitionChatResponseDTO {
    
    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("obsession_type")
    private String obsessionType;

    @JsonProperty("encouragement_message")
    private String encouragementMessage;

    @JsonProperty("cognitive_error_explanation")
    private String cognitiveErrorExplanation;

    @JsonProperty("personalized_situation")
    private String personalizedSituation;

    @JsonProperty("emotion_question")
    private String emotionQuestion;

    // Step 3 응답 필드들
    @JsonProperty("empathy_message")
    private String empathyMessage;

    @JsonProperty("insight_message")
    private String insightMessage;

    @JsonProperty("choice_question")
    private String choiceQuestion;

    // Step 2 응답 필드들
    @JsonProperty("cognitive_errors")
    private Object cognitiveErrors;

    @JsonProperty("reflection_question")
    private String reflectionQuestion;

    @JsonProperty("reality_check_question")
    private String realityCheckQuestion;

    // Step 4 응답 필드
    @JsonProperty("response")
    private String response;

    // Step 5 응답 필드들
    @JsonProperty("intro_message")
    private String introMessage;

    @JsonProperty("cognitive_restructuring")
    private Object cognitiveRestructuring;

    @JsonProperty("final_encouragement")
    private String finalEncouragement;

    // 대화 마무리 응답 필드
    @JsonProperty("completion_message")
    private String completionMessage;
}
