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
public class ReportRequestDTO {
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("session_duration_seconds")
    private Integer sessionDurationSeconds; // 반응방지 시간 (초)
}
