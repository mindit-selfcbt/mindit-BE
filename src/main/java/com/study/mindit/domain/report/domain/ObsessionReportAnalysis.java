package com.study.mindit.domain.report.domain;

import com.study.mindit.global.domain.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "obsession_report_analysis")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObsessionReportAnalysis extends BaseDocument {

    @Field("session_id")
    private String sessionId;
    
    @Field("obsession_type")
    private String obsessionType;
    
    @Field("anxiety_thoughts")
    private String anxietyThoughts;
    
    @Field("compulsive_behaviors")
    private String compulsiveBehaviors;
    
    @Field("anxiety_hierarchy")
    private List<AnxietyHierarchy> anxietyHierarchy;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnxietyHierarchy {
        @Field("situation")
        private String situation;
        
        @Field("score")
        private Integer score;
    }
}

