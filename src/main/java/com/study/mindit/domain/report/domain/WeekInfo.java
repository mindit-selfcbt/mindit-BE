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
public class WeekInfo {
    
    @Field("week")
    private Integer week;
    
    @Field("start_date")
    private String startDate;
    
    @Field("end_date")
    private String endDate;
}
