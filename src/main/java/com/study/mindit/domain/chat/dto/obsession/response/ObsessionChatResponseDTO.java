package com.study.mindit.domain.chat.dto.obsession.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonPropertyOrder({
    "question",
    "choices",
    "response",
    "gratitude_message",
    "user_pattern_summary",
    "thought_examples",
    "category_message",
    "encouragement",
    "intro_message",
    "situations",
    "anxiety_hierarchy",
    "practice_message",
    "example_message",
    "support_message"
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObsessionChatResponseDTO {
    private String question;

    private List<String> choices;

    private String response;

    @JsonProperty("gratitude_message")
    private String gratitudeMessage;

    @JsonProperty("user_pattern_summary")
    private String userPatternSummary;

    @JsonProperty("thought_examples")
    private List<String> thoughtExamples;

    @JsonProperty("category_message")
    private String categoryMessage;

    @JsonProperty("encouragement")
    private String encouragement;

    @JsonProperty("intro_message")
    private String introMessage;

    @JsonProperty("situations")
    private List<String> situations;

    @JsonProperty("anxiety_hierarchy")
    private List<ObsessionAnxietyHierarchyDTO> anxietyHierarchy;

    @JsonProperty("practice_message")
    private String practiceMessage;

    @JsonProperty("example_message")
    private String exampleMessage;

    @JsonProperty("support_message")
    private String supportMessage;

    @JsonIgnore
    private String sessionId;
}
