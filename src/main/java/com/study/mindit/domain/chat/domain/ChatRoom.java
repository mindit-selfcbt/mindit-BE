package com.study.mindit.domain.chat.domain;

import com.study.mindit.global.domain.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "chat_rooms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends BaseDocument {

    @Field("room_type")
    private RoomType roomType;

    @Field("session_type")
    private String sessionId;
    
    @Field("conversation_history")
    @Builder.Default
    private List<ConversationItem> conversationHistory = new ArrayList<>();
    
    @Field("current_step")
    @Builder.Default
    private int currentStep = 0;
    
    @Field("temp_selected_situations")
    private List<String> tempSelectedSituations;
    
    // 대화 추가 메서드
    public void addConversation(String role, Object content) {
        if (this.conversationHistory == null) {
            this.conversationHistory = new ArrayList<>();
        }
        this.conversationHistory.add(
            ConversationItem.builder()
                .role(role)
                .content(content)
                .build()
        );
    }
    
    // Step 증가
    public void incrementStep() {
        this.currentStep++;
    }
} 