package com.study.mindit.domain.chat.domain.repository;

import com.study.mindit.domain.chat.domain.ChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, String> {
    Flux<ChatRoom> findAll();
    Mono<ChatRoom> findBySessionId(String sessionId);
} 