package com.study.mindit.domain.report.domain.repository;

import com.study.mindit.domain.report.domain.ObsessionReportAnalysis;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ObsessionReportAnalysisRepository extends ReactiveMongoRepository<ObsessionReportAnalysis, String> {
    
    //세션 ID로 레포트 분석을 조회
    Mono<ObsessionReportAnalysis> findBySessionId(String sessionId);
    
    //세션 ID로 모든 레포트 분석을 조회
    Flux<ObsessionReportAnalysis> findAllBySessionId(String sessionId);
    
    //강박 유형으로 모든 레포트 분석을 조회
    Flux<ObsessionReportAnalysis> findAllByObsessionType(String obsessionType);
}

