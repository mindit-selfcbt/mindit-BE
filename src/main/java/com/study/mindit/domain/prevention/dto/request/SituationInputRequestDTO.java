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
public class SituationInputRequestDTO {
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("obsession_situation")
    private String obsessiveSituation; // 강박상황 (위계표 선택 또는 직접 입력)
}
