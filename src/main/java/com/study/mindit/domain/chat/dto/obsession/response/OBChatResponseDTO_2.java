package com.study.mindit.domain.chat.dto.obsession.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({
    "response"
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OBChatResponseDTO_2 {

    private String response;

    @JsonIgnore
    private String sessionId;
}
