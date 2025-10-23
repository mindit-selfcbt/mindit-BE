package com.study.mindit.domain.report.presentation;

import com.study.mindit.domain.report.application.ObsessionReportAnalysisService;
import com.study.mindit.domain.report.domain.ObsessionReportAnalysis;
import com.study.mindit.domain.report.domain.repository.ObsessionReportAnalysisRepository;
import com.study.mindit.domain.report.dto.obsession.response.ObsessionReportAnalysisResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/report/anxiety")
@RequiredArgsConstructor
@Tag(name = "Obsession Anxiety Report", description = "불안위계표")
public class ObsessionReportAnalysisController {

    private final ObsessionReportAnalysisService obsessionReportAnalysisService;
    private final ObsessionReportAnalysisRepository obsessionReportAnalysisRepository;

    // 세션 ID로 obsession 채팅 결과를 조회하여 불안위계표 생성
    @PostMapping("/generate/{sessionId}")
    @Operation(summary = "불안위계표 생성", description = "세션 ID로 채팅 내역을 조회하여 불안위계표를 생성합니다.")
    public Mono<ResponseEntity<ObsessionReportAnalysisResponseDTO>> generateReportAnalysis(
            @PathVariable String sessionId) {
        
        log.info("=== Obsession 레포트 분석 생성 API 호출 ===");
        log.info("세션 ID: {}", sessionId);
        
        return obsessionReportAnalysisService.generateReportAnalysis(sessionId)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("=== Obsession 레포트 분석 생성 API 성공 ==="))
                .doOnError(error -> log.error("=== Obsession 레포트 분석 생성 API 실패 ===", error))
                .onErrorResume(error -> {
                    log.error("Obsession 레포트 분석 생성 중 오류 발생: {}", error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // 세션 ID로 불안위계표를 조회
    @GetMapping("/{sessionId}")
    @Operation(summary = "특정 불안위계표 조회", description = "세션 ID로 불안위계표를 조회합니다.")
    public Mono<ResponseEntity<ObsessionReportAnalysis>> getReportAnalysisBySessionId(
            @PathVariable String sessionId) {
        
        log.info("=== 레포트 분석 조회 API 호출 ===");
        log.info("세션 ID: {}", sessionId);
        
        return obsessionReportAnalysisRepository.findBySessionId(sessionId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .doOnSuccess(response -> log.info("=== 레포트 분석 조회 API 성공 ==="))
                .doOnError(error -> log.error("=== 레포트 분석 조회 API 실패 ===", error))
                .onErrorResume(error -> {
                    log.error("레포트 분석 조회 중 오류 발생: {}", error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // 강박 유형별로 모든 불안위계표를 조회합니다.
    @GetMapping("/type/{obsessionType}")
    @Operation(summary = "유형별 불안위계표 조회", description = "강박 유형별 모든 불안위계표를 조회합니다.")
    public Mono<ResponseEntity<Flux<ObsessionReportAnalysis>>> getReportAnalysesByObsessionType(
            @PathVariable String obsessionType) {
        
        log.info("=== 강박 유형별 레포트 분석 조회 API 호출 ===");
        log.info("강박 유형: {}", obsessionType);
        
        Flux<ObsessionReportAnalysis> reportAnalyses = obsessionReportAnalysisRepository.findAllByObsessionType(obsessionType);
        
        return Mono.just(ResponseEntity.ok(reportAnalyses))
                .doOnSuccess(response -> log.info("=== 강박 유형별 레포트 분석 조회 API 성공 ==="))
                .doOnError(error -> log.error("=== 강박 유형별 레포트 분석 조회 API 실패 ===", error));
    }
}
