package com.study.mindit.domain.chat.dto.obsession.request;

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
public class OBChatRequestDTO_2 {

    @JsonProperty("conversation_history")
    private List<OBConversationDTO> conversationHistory;

    @JsonProperty("session_id")
    private String sessionId;
}
