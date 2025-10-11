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
public class OBChatResponseDTO_5 {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("intro_message")
    private String introMessage;

    @JsonProperty("question")
    private String question;

    @JsonProperty("situations")
    private List<String> situations;
}


