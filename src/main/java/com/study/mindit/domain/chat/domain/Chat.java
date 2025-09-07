package com.study.mindit.domain.chat.domain;

import com.study.mindit.global.domain.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "chat")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat extends BaseDocument {

	@Field("room_id")
	private String roomId;

	@Field("sender")
	private SenderType sender;

	@Field("content")
	private String content;

	@Field("step")
	private int step;
}
