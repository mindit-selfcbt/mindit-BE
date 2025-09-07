package com.study.mindit.domain.chat.dto;

import com.study.mindit.domain.chat.domain.Chat;
import com.study.mindit.domain.chat.domain.SenderType;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {

    private String roomId;
    private String content;

    public Chat to() {
        return Chat.builder()
                .roomId(this.roomId)
                .content(this.content)
                .build();
    }
} 