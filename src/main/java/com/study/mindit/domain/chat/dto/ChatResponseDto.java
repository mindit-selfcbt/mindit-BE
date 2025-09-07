package com.study.mindit.domain.chat.dto;

import com.study.mindit.domain.chat.domain.Chat;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDto {
    private String content;

    public static ChatResponseDto from(Chat chat) {
        return ChatResponseDto.builder()
                .content(chat.getContent())
                .build();
    }
} 