package com.study.mindit.domain.report.domain;

import com.study.mindit.global.domain.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "obsession_reports")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObsessionReport extends BaseDocument {

    @Field("session_id")
    private String sessionId;
    
    @Field("obsession_type")
    private String obsessionType;
    
    @Field("training_explanation")
    private TrainingExplanation trainingExplanation;
    
    @Field("weeks")
    private List<WeekInfo> weeks;
    
    @Field("training_plan")
    private List<TrainingPlan> trainingPlan;
    
    @Field("report_type")
    private ReportType reportType; // REPORT_1 또는 REPORT_2
}
