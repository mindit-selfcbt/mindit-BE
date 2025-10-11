package com.study.mindit.domain.chat.dto.obsession.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OBConversationDTO {

    private String role;

    private Object content;
}
