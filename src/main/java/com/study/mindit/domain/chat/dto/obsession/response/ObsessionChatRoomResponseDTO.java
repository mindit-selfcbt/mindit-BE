package com.study.mindit.domain.chat.dto.obsession.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.mindit.domain.chat.domain.ObsessionChatRoom;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObsessionChatRoomResponseDTO {

    @JsonProperty("session_id")
    private String sessionId;

    public static ObsessionChatRoomResponseDTO from(ObsessionChatRoom chatRoom) {
        return ObsessionChatRoomResponseDTO.builder()
                .sessionId(chatRoom.getId())
                .build();
    }
} 