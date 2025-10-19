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
    "intro_message",
    "question",
    "situations"
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObsessionChatResponseDTO_7 {

    @JsonIgnore
    private String sessionId;

    @JsonProperty("intro_message")
    private String introMessage;

    @JsonProperty("question")
    private String question;

    @JsonProperty("situations")
    private List<String> situations;
}


