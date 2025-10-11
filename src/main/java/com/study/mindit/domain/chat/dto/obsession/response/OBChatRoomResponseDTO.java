package com.study.mindit.domain.chat.dto.obsession.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.mindit.domain.chat.domain.OBChatRoom;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OBChatRoomResponseDTO {

    @JsonProperty("session_id")
    private String sessionId;

    public static OBChatRoomResponseDTO from(OBChatRoom chatRoom) {
        return OBChatRoomResponseDTO.builder()
                .sessionId(chatRoom.getId())
                .build();
    }
} 