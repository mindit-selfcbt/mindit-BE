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
public class TrainingMethod {
    
    @Field("title")
    private String title;
    
    @Field("description")
    private String description;
    
    @Field("when")
    private String when;
    
    @Field("instruction")
    private String instruction;
}
