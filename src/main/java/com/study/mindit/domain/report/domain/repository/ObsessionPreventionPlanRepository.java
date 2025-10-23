package com.study.mindit.domain.report.domain.repository;

import com.study.mindit.domain.report.domain.ObsessionPreventionPlan;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ObsessionPreventionPlanRepository extends ReactiveMongoRepository<ObsessionPreventionPlan, String> {
    
    //세션 ID로 예방 계획을 조회
    Mono<ObsessionPreventionPlan> findBySessionId(String sessionId);
    
    //세션 ID로 모든 예방 계획을 조회
    Flux<ObsessionPreventionPlan> findAllBySessionId(String sessionId);
    
    //강박 유형으로 모든 예방 계획을 조회
    Flux<ObsessionPreventionPlan> findAllByObsessionType(String obsessionType);
}

