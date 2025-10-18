package com.study.mindit.domain.prevention.domain.repository;

import com.study.mindit.domain.prevention.domain.PreventionReport;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PreventionReportRepository extends ReactiveMongoRepository<PreventionReport, String> {
    
    Flux<PreventionReport> findBySessionIdOrderByCreatedAtDesc(String sessionId);
    
    Mono<Long> countBySessionIdAndWeekNumber(String sessionId, Integer weekNumber);

    @Query("{ 'session_id': ?0, 'is_completed': false }")
    Mono<PreventionReport> findOngoingReportBySessionId(String sessionId);
}

