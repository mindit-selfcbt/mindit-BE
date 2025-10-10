package com.study.mindit.domain.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnxietyHierarchyItemDTO {

    @JsonProperty("order")
    private Integer order;

    @JsonProperty("situation")
    private String situation;

    @JsonProperty("score")
    private Integer score;
}


