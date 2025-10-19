package com.study.mindit.domain.report.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlan {
    
    @Field("week")
    private Integer week;
    
    @Field("training_name")
    private String trainingName;
    
    @Field("goal")
    private String goal;
    
    @Field("content")
    private String content;
}
