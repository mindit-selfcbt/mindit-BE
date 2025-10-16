package com.study.mindit.domain.prevention.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.mindit.domain.prevention.domain.AnxietyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnxietyInputRequestDTO {
    
    @JsonProperty("session_id")
    @NotNull(message = "세션 ID는 필수입니다")
    private String sessionId;
    
    @JsonProperty("anxiety_level")
    @Min(value = 0, message = "불안정도는 0 이상이어야 합니다")
    @Max(value = 100, message = "불안정도는 100 이하여야 합니다")
    private Integer anxietyLevel; // 불안정도 점수 (0-100)
    
    @NotNull(message = "타입은 필수입니다")
    private AnxietyType anxietyType;     // BEFORE 또는 AFTER
}
