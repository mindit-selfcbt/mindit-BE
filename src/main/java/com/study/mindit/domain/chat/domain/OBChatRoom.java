package com.study.mindit.domain.chat.domain;

import com.study.mindit.global.domain.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "chat_rooms")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OBChatRoom extends BaseDocument {

    @Field("room_type")
    private RoomType roomType;
    
    @Field("conversation_history")
    @Builder.Default
    private List<OBConversation> conversationHistory = new ArrayList<>();
    
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
            OBConversation.builder()
                .role(role)
                .content(content)
                .build()
        );
    }
    
    // Step 증가
    public void incrementStep() {
        this.currentStep++;
    }
    
    // 임시 선택된 상황들 설정
    public void setTempSelectedSituations(List<String> situations) {
        this.tempSelectedSituations = situations;
    }
} 