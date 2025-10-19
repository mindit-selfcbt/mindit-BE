package com.study.mindit.domain.chat.dto.cognition.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CognitionChatRequestDTO_1 {

    @JsonProperty("session_id")
    @NotNull(message = "세션 ID는 필수입니다")
    private String sessionId;

}