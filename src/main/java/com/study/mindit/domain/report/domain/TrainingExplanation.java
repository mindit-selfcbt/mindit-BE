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
public class TrainingExplanation {
    
    @Field("response_prevention")
    private TrainingMethod responsePrevention;
    
    @Field("ai_ar_training")
    private TrainingMethod aiArTraining;
    
    @Field("imagination_training")
    private TrainingMethod imaginationTraining;
}
