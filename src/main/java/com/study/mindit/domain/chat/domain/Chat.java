package com.study.mindit.domain.chat.domain;

import com.study.mindit.global.domain.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "chat")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat extends BaseDocument {

	@Field("session_id")
	private String sessionId;

	@Field("sender")
	private SenderType sender;

	@Field("step")
	private int step;

	@Field("content")
	private String content;

	@Field("question")
	private String question;

	@Field("choices")
	private List<String> choices;

	@Field("user_pattern_summary")
	private String userPatternSummary;

	@Field("thought_examples")
	private List<String> thoughtExamples;
}
