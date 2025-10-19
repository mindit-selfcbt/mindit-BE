package com.study.mindit.domain.chat.dto.cognition.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CognitiveRestructuringDTO {
    
    @JsonProperty("old_thought")
    private String oldThought;

    @JsonProperty("new_perspective")
    private String newPerspective;
}
