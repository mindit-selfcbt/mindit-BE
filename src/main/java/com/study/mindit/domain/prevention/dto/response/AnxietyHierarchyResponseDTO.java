package com.study.mindit.domain.prevention.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnxietyHierarchyResponseDTO {
    
    private String id;
    
    private Integer order;
    
    private String situation;
    
    private Integer score;
    
}
