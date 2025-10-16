package com.study.mindit.domain.prevention.domain.repository;

import com.study.mindit.domain.prevention.domain.PreventionReport;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PreventionReportRepository extends ReactiveMongoRepository<PreventionReport, String> {
    
    Flux<PreventionReport> findBySessionId(String sessionId);
    
    Flux<PreventionReport> findBySessionIdOrderByCreatedAtDesc(String sessionId);
    
    Mono<PreventionReport> findBySessionIdAndWeekNumberAndSessionNumberInWeek(String sessionId, Integer weekNumber, Integer sessionNumberInWeek);
    
    Flux<PreventionReport> findBySessionIdAndWeekNumber(String sessionId, Integer weekNumber);
    
    Mono<Long> countBySessionIdAndWeekNumber(String sessionId, Integer weekNumber);
}

