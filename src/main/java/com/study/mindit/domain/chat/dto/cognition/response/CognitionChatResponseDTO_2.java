package com.study.mindit.domain.chat.dto.cognition.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CognitionChatResponseDTO_2 {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("cognitive_errors")
    private List<CognitiveErrorDTO> cognitiveErrors;

    @JsonProperty("reflection_question")
    private String reflectionQuestion;

    @JsonProperty("reality_check_question")
    private String realityCheckQuestion;
}

