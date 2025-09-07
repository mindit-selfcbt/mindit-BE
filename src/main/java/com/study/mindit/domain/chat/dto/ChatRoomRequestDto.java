package com.study.mindit.domain.chat.dto;

import com.study.mindit.domain.chat.domain.ChatRoom;
import lombok.*;


@Getter
@Builder
public class ChatRoomRequestDto {
 
    public ChatRoom to() {
        return ChatRoom.builder()
                .build();
    }
} 