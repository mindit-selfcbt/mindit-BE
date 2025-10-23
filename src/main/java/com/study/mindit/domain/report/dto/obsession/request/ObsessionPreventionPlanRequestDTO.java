package com.study.mindit.domain.report.dto.obsession.request;

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
public class ObsessionPreventionPlanRequestDTO {
    
    @JsonProperty("conversation_history")
    private List<ConversationMessage> conversationHistory;
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationMessage {
        @JsonProperty("role")
        private String role;
        
        @JsonProperty("content")
        private Object content;
    }
}

