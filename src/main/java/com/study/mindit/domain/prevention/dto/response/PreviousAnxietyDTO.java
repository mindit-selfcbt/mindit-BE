package com.study.mindit.domain.prevention.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviousAnxietyDTO {
    
    @JsonProperty("anxiety_level_start")
    private Integer beforeAnxietyLevel;
    
    @JsonProperty("anxiety_level_end")
    private Integer afterAnxietyLevel;
    
    @JsonProperty("session_date")
    private String sessionDate; // "월/일" 형태
}
