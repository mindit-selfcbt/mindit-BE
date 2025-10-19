package com.study.mindit.domain.report.dto.obsession.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObsessionReportRequestDTO {
    
    @JsonProperty("session_id")
    private String sessionId;
}
