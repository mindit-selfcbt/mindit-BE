package com.study.mindit.domain.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationItem {

    @Field("role")
    private String role;  // "user" or "assistant"

    @Field("content")
    private Object content;  // String 또는 구조화된 객체 모두 허용
}

