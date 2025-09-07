package com.study.mindit.domain.chat.dto;

import com.study.mindit.domain.chat.domain.ChatRoom;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {

    private String roomId;

    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getId())
                .build();
    }
} 