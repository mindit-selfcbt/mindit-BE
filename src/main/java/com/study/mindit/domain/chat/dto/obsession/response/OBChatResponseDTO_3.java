package com.study.mindit.domain.chat.dto.obsession.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonPropertyOrder({
    "gratitude_message",
    "user_pattern_summary",
    "question",
    "thought_examples"
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OBChatResponseDTO_3 {

    @JsonProperty("gratitude_message")
    private String gratitudeMessage;

    @JsonProperty("user_pattern_summary")
    private String userPatternSummary;

    private String question;

    @JsonProperty("thought_examples")
    private List<String> thoughtExamples;

    @JsonIgnore
    private String sessionId;
}
