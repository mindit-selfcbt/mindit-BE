package com.study.mindit.domain.chat.domain.repository;

import com.study.mindit.domain.chat.domain.ObsessionChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ObsessionChatRoomRepository extends ReactiveMongoRepository<ObsessionChatRoom, String> {
    Flux<ObsessionChatRoom> findAll();
} 