package com.study.mindit.domain.chat.domain.repository;

import com.study.mindit.domain.chat.domain.Chat;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {
    Flux<Chat> findBySessionId(String sessionId);
}
