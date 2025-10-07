package com.study.mindit.domain.chat.dto.response;

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
public class ChatResponseDTO_3 {

    @JsonProperty("gratitude_message")
    private String gratitudeMessage;

    @JsonProperty("user_pattern_summary")
    private String userPatternSummary;

    private String question;

    @JsonProperty("thought_examples")
    private List<String> thoughtExamples;

    @JsonProperty("session_id")
    private String sessionId;
}
