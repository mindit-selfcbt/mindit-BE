package com.study.mindit.domain.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.mindit.domain.chat.domain.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatResponseDTO {
    private String content;

    private String question;

    private List<String> choices;

    @JsonProperty("user_pattern_summary")
    private String userPatternSummary;

    @JsonProperty("thought_examples")
    private List<String> thoughtExamples;

    @JsonProperty("session_id")
    private String sessionId;

    private Integer step;

    public static ChatResponseDTO from(Chat chat) {
        return ChatResponseDTO.builder()
                .content(chat.getContent())
                .sessionId(chat.getSessionId())
                .step(chat.getStep())
                .question(chat.getQuestion())
                .choices(chat.getChoices())
                .userPatternSummary(chat.getUserPatternSummary())
                .thoughtExamples(chat.getThoughtExamples())
                .build();
    }
}
