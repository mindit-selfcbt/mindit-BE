package com.study.mindit.domain.chat.dto.cognition.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CognitionConversationDTO {
    private String role;
    private Object content;
}

