package com.study.mindit.domain.chat.dto.obsession.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonPropertyOrder({
    "question",
    "choices"
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObsessionChatResponseDTO_1 {
    private String question;

    private List<String> choices;

    @JsonIgnore
    private String sessionId;
}
