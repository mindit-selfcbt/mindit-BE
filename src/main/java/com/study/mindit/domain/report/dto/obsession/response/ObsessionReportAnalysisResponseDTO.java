package com.study.mindit.domain.report.dto.obsession.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObsessionReportAnalysisResponseDTO {
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("obsession_type")
    private String obsessionType;
    
    @JsonProperty("anxiety_thoughts")
    private String anxietyThoughts;
    
    @JsonProperty("compulsive_behaviors")
    private String compulsiveBehaviors;
    
    @JsonProperty("anxiety_hierarchy")
    private List<AnxietyHierarchy> anxietyHierarchy;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnxietyHierarchy {
        @JsonProperty("situation")
        private String situation;
        
        @JsonProperty("score")
        private Integer score;
    }
}

