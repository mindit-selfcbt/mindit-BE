package com.study.mindit.domain.prevention.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {
    
    // 강박상황
    @JsonProperty("obsession_situation")
    private String obsessiveSituation;
    
    // 반응방지 횟수 (이번 주)
    @JsonProperty("this_week_count")
    private Integer thisWeekCount;
    
    // 반응방지 시간
    @JsonProperty("session_duration")
    private String sessionDuration; // "분/초" 형태
    
    // 이전 불안정도
    @JsonProperty("anxiety_level_start")
    private Integer beforeAnxietyLevel;
    
    // 이번 불안정도  
    @JsonProperty("anxiety_level_end")
    private Integer afterAnxietyLevel;
    
    // 강박사고
    @JsonProperty("obsession_thought")
    private String obsessiveThought;
    
    // 이전 세션들의 불안정도 비교 데이터 (최신순 3개)
    @JsonProperty("previous_anxiety_data")
    private List<PreviousAnxietyDTO> previousAnxietyData;
    
    // 리포트 생성 시간
    @JsonProperty("report_time")
    private LocalDateTime reportTime;
}
