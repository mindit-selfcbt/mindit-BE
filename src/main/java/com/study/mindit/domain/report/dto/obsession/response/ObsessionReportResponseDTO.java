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
public class ObsessionReportResponseDTO {
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("obsession_type")
    private String obsessionType;
    
    @JsonProperty("training_explanation")
    private TrainingExplanation trainingExplanation;
    
    @JsonProperty("weeks")
    private List<WeekInfo> weeks;
    
    @JsonProperty("training_plan")
    private List<TrainingPlan> trainingPlan;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrainingExplanation {
        @JsonProperty("response_prevention")
        private TrainingMethod responsePrevention;
        
        @JsonProperty("ai_ar_training")
        private TrainingMethod aiArTraining;
        
        @JsonProperty("imagination_training")
        private TrainingMethod imaginationTraining;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrainingMethod {
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("when")
        private String when;
        
        @JsonProperty("instruction")
        private String instruction;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeekInfo {
        @JsonProperty("week")
        private Integer week;
        
        @JsonProperty("start_date")
        private String startDate;
        
        @JsonProperty("end_date")
        private String endDate;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrainingPlan {
        @JsonProperty("week")
        private Integer week;
        
        @JsonProperty("training_name")
        private String trainingName;
        
        @JsonProperty("goal")
        private String goal;
        
        @JsonProperty("content")
        private String content;
    }
}
