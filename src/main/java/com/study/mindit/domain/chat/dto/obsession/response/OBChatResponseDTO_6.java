package com.study.mindit.domain.chat.dto.obsession.response;

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
public class OBChatResponseDTO_6 {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("intro_message")
    private String introMessage;

    @JsonProperty("anxiety_hierarchy")
    private List<OBAnxietyHierarchyDTO> anxietyHierarchy;

    @JsonProperty("practice_message")
    private String practiceMessage;

    @JsonProperty("example_message")
    private String exampleMessage;

    @JsonProperty("support_message")
    private String supportMessage;
}


