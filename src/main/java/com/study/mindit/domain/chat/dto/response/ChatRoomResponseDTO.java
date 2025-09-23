package com.study.mindit.domain.chat.dto.response;

import com.study.mindit.domain.chat.domain.ChatRoom;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDTO {

    private String sessionId;

    public static ChatRoomResponseDTO from(ChatRoom chatRoom) {
        return ChatRoomResponseDTO.builder()
                .sessionId(chatRoom.getId())
                .build();
    }
} 