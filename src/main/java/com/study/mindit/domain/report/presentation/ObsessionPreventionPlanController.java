package com.study.mindit.domain.report.presentation;

import com.study.mindit.domain.report.application.ObsessionPreventionPlanService;
import com.study.mindit.domain.report.domain.ObsessionPreventionPlan;
import com.study.mindit.domain.report.domain.TrainingPlan;
import com.study.mindit.domain.report.domain.repository.ObsessionPreventionPlanRepository;
import com.study.mindit.domain.report.dto.obsession.response.ObsessionPreventionPlanResponseDTO;
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
@RequestMapping("/api/report/plan")
@RequiredArgsConstructor
@Tag(name = "Obsession Plan Report", description = "강박 개선 플랜 API")
public class ObsessionPreventionPlanController {

    private final ObsessionPreventionPlanService obsessionPreventionPlanService;
    private final ObsessionPreventionPlanRepository obsessionPreventionPlanRepository;

    // 세션 ID로 강박 개선 계획을 생성
    @PostMapping("/generate/{sessionId}")
    @Operation(summary = "강박 개선 계획 생성", description = "세션 ID로 obsession 채팅 결과를 조회하여 강박 개선 계획을 생성합니다.")
    public Mono<ResponseEntity<ObsessionPreventionPlanResponseDTO>> generatePreventionPlan(
            @PathVariable String sessionId) {
        
        log.info("=== Obsession 예방 계획 생성 API 호출 ===");
        log.info("세션 ID: {}", sessionId);
        
        return obsessionPreventionPlanService.generatePreventionPlan(sessionId)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("=== Obsession 예방 계획 생성 API 성공 ==="))
                .doOnError(error -> log.error("=== Obsession 예방 계획 생성 API 실패 ===", error))
                .onErrorResume(error -> {
                    log.error("Obsession 예방 계획 생성 중 오류 발생: {}", error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // 세션 ID로 강박 개선 계획을 조회
    @GetMapping("/{sessionId}")
    @Operation(summary = "강박 개선 계획 조회", description = "세션 ID로 강박 개선 계획을 조회합니다.")
    public Mono<ResponseEntity<ObsessionPreventionPlan>> getPreventionPlanBySessionId(
            @PathVariable String sessionId) {
        
        log.info("=== 예방 계획 조회 API 호출 ===");
        log.info("세션 ID: {}", sessionId);
        
        return obsessionPreventionPlanRepository.findBySessionId(sessionId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .doOnSuccess(response -> log.info("=== 예방 계획 조회 API 성공 ==="))
                .doOnError(error -> log.error("=== 예방 계획 조회 API 실패 ===", error))
                .onErrorResume(error -> {
                    log.error("예방 계획 조회 중 오류 발생: {}", error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // 유형별 모든 예방 계획을 조회합니다.
    @GetMapping("/type/{obsessionType}")
    @Operation(summary = "유형별 강박 개선 계획 조회", description = "유형별 전체 개선 계획을 조회합니다.")
    public Mono<ResponseEntity<Flux<ObsessionPreventionPlan>>> getPreventionPlansByObsessionType(
            @PathVariable String obsessionType) {
        
        log.info("=== 강박 유형별 예방 계획 조회 API 호출 ===");
        log.info("강박 유형: {}", obsessionType);
        
        Flux<ObsessionPreventionPlan> preventionPlans = obsessionPreventionPlanRepository.findAllByObsessionType(obsessionType);
        
        return Mono.just(ResponseEntity.ok(preventionPlans))
                .doOnSuccess(response -> log.info("=== 강박 유형별 예방 계획 조회 API 성공 ==="))
                .doOnError(error -> log.error("=== 강박 유형별 예방 계획 조회 API 실패 ===", error));
    }

    // 특정 주차의 강박 개선 계획을 조회합니다.
    @GetMapping("/{sessionId}/week/{week}")
    @Operation(summary = "주차별 강박 개선 계획 조회", description = "주차별 특정 강박 개선 계획을 조회합니다.")
    public Mono<ResponseEntity<Object>> getPreventionPlanBySessionIdAndWeek(
            @PathVariable String sessionId,
            @PathVariable int week) {
        
        log.info("=== 주차별 예방 계획 조회 API 호출 ===");
        log.info("세션 ID: {}, 주차: {}", sessionId, week);
        
        return obsessionPreventionPlanRepository.findBySessionId(sessionId)
                .flatMap(plan -> {
                    TrainingPlan weeklyPlan = plan.getTrainingPlan().stream()
                            .filter(tp -> tp.getWeek() == week)
                            .findFirst()
                            .orElse(null);
                    
                    if (weeklyPlan != null) {
                        log.info("=== 주차별 예방 계획 조회 API 성공 (주차: {}) ===", week);
                        return Mono.just(ResponseEntity.ok((Object) weeklyPlan));
                    } else {
                        log.warn("=== 주차별 예방 계획 조회 API 실패: 해당 주차의 계획을 찾을 수 없음 (세션 ID: {}, 주차: {}) ===", sessionId, week);
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(error -> {
                    log.error("=== 주차별 예방 계획 조회 API 실패 (세션 ID: {}, 주차: {}) ===", sessionId, week, error);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
