package com.study.mindit.domain.chat.domain.repository;

import com.study.mindit.domain.chat.domain.OBChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface OBChatRoomRepository extends ReactiveMongoRepository<OBChatRoom, String> {
    Flux<OBChatRoom> findAll();
} 