package com.study.mindit.domain.chat.dto.obsession.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OBConversationDTO {

    private String role;

    private Object content;  // String 또는 구조화된 객체 모두 허용
}
