package com.study.mindit.domain.report.presentation;

import com.study.mindit.domain.report.application.ObsessionReportService;
import com.study.mindit.domain.report.dto.obsession.request.ObsessionReportRequestDTO;
import com.study.mindit.domain.report.dto.obsession.response.ObsessionReportResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/report/obsession")
@RequiredArgsConstructor
@Tag(name = "ObsessionReport", description = "Obsession 레포트 생성 API")
public class ObsessionReportController {

    private final ObsessionReportService obsessionReportService;

    /**
     * 세션 ID로 obsession 채팅 결과를 조회하여 레포트1을 생성합니다.
     * 
     * @param reportRequest 세션 ID가 포함된 요청
     * @return 생성된 레포트1 정보
     */
    @PostMapping("/generate")
    @Operation(summary = "Obsession 레포트1 생성", description = "세션 ID로 obsession 채팅 결과를 조회하여 훈련 계획 레포트1을 생성하고 DB에 저장합니다.")
    public Mono<ResponseEntity<ObsessionReportResponseDTO>> generateReport(
            @RequestBody ObsessionReportRequestDTO reportRequest) {
        
        log.info("=== Obsession 레포트1 생성 API 호출 ===");
        log.info("세션 ID: {}", reportRequest.getSessionId());
        
        return obsessionReportService.generateReport(reportRequest)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("=== Obsession 레포트1 생성 API 성공 ==="))
                .doOnError(error -> log.error("=== Obsession 레포트1 생성 API 실패 ===", error))
                .onErrorResume(error -> {
                    log.error("Obsession 레포트1 생성 중 오류 발생: {}", error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    /**
     * 세션 ID로 obsession 채팅 결과를 조회하여 레포트2를 생성합니다.
     * 
     * @param reportRequest 세션 ID가 포함된 요청
     * @return 생성된 레포트2 정보
     */
    @PostMapping("/generate2")
    @Operation(summary = "Obsession 레포트2 생성", description = "세션 ID로 obsession 채팅 결과를 조회하여 훈련 계획 레포트2를 생성하고 DB에 저장합니다.")
    public Mono<ResponseEntity<ObsessionReportResponseDTO>> generateReport2(
            @RequestBody ObsessionReportRequestDTO reportRequest) {
        
        log.info("=== Obsession 레포트2 생성 API 호출 ===");
        log.info("세션 ID: {}", reportRequest.getSessionId());
        
        return obsessionReportService.generateReport2(reportRequest)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("=== Obsession 레포트2 생성 API 성공 ==="))
                .doOnError(error -> log.error("=== Obsession 레포트2 생성 API 실패 ===", error))
                .onErrorResume(error -> {
                    log.error("Obsession 레포트2 생성 중 오류 발생: {}", error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}