package com.study.mindit.domain.prevention.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThoughtInputRequestDTO {
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("obsession_thought")
    private String obsessiveThought; // 강박사고 내용
}
