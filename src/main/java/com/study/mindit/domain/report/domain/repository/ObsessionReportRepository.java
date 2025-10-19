package com.study.mindit.domain.report.domain.repository;

import com.study.mindit.domain.report.domain.ObsessionReport;
import com.study.mindit.domain.report.domain.ReportType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ObsessionReportRepository extends ReactiveMongoRepository<ObsessionReport, String> {
    
    //세션 ID로 레포트를 조회합
    Mono<ObsessionReport> findBySessionId(String sessionId);
    
    //세션 ID와 레포트 타입으로 레포트를 조회
    Mono<ObsessionReport> findBySessionIdAndReportType(String sessionId, ReportType reportType);
    
    //세션 ID로 모든 레포트를 조회
    Flux<ObsessionReport> findAllBySessionId(String sessionId);
    
    // 레포트 타입으로 모든 레포트를 조회
    Flux<ObsessionReport> findAllByReportType(ReportType reportType);
}
