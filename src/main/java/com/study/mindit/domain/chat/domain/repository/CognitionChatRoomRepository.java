package com.study.mindit.domain.chat.domain.repository;

import com.study.mindit.domain.chat.domain.CognitionChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CognitionChatRoomRepository extends ReactiveMongoRepository<CognitionChatRoom, String> {
}

