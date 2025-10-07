package com.study.mindit.domain.chat.dto.request;

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
public class ChatRequestDTO_2 {

    private List<ConversationItem> conversationHistory;

    private String sessionId;

    public Chat toEntity() {
        if (conversationHistory == null || conversationHistory.isEmpty()) {
            return null;
        }
        ConversationItem lastItem = conversationHistory.get(conversationHistory.size() - 1);

        return Chat.builder()
                .sessionId(this.sessionId)
                .content(lastItem.getContent())
                .build();
    }
}
