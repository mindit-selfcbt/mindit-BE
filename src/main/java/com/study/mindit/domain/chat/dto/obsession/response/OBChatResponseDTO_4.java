package com.study.mindit.domain.chat.dto.obsession.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({
    "user_pattern_summary",
    "category_message",
    "encouragement"
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OBChatResponseDTO_4 {

    @JsonIgnore
    private String sessionId;

    @JsonProperty("user_pattern_summary")
    private String userPatternSummary;

    @JsonProperty("category_message")
    private String categoryMessage;

    @JsonProperty("encouragement")
    private String encouragement;
}


