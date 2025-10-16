package com.study.mindit.domain.prevention.domain;

import com.study.mindit.global.domain.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "prevention_reports")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PreventionReport extends BaseDocument {

    @Field("session_id")
    private String sessionId;

    @Field("obsession_situation")
    private String obsessionSituation;

    @Field("obsession_thought")
    private String obsessionThought;

    @Field("anxiety_level_start")
    private Integer anxietyLevelStart;

    @Field("anxiety_level_end")
    private Integer anxietyLevelEnd;

    @Field("session_duration_seconds")
    private Integer sessionDurationSeconds;

    @Field("session_start_time")
    private LocalDateTime sessionStartTime;

    @Field("session_end_time")
    private LocalDateTime sessionEndTime;

    @Field("week_number")
    private Integer weekNumber;

    @Field("session_number_in_week")
    private Integer sessionNumberInWeek;

    @Field("is_completed")
    @Builder.Default
    private Boolean isCompleted = false;
}

