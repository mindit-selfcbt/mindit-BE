package com.study.mindit.domain.prevention.presentation;

import com.study.mindit.domain.prevention.application.PreventionService;
import com.study.mindit.domain.prevention.dto.request.*;
import com.study.mindit.domain.prevention.dto.response.ReportResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/prevention")
@RequiredArgsConstructor
@Tag(name = "Prevention", description = "반응방지 API")
public class PreventionController {

    private final PreventionService preventionService;

    @PostMapping("/anxiety-input")
    @Operation(summary = "1. 불안정도 입력", description = "이전 또는 이번 불안정도를 입력합니다.")
    public Mono<ResponseEntity<String>> inputAnxietyLevel(@RequestBody AnxietyInputRequestDTO request) {
        log.info("불안정도 입력 요청 - sessionId: {}, type: {}, level: {}",
                request.getSessionId(), request.getAnxietyType(), request.getAnxietyLevel());
        return preventionService.inputAnxietyLevel(request)
                .map(ResponseEntity::ok);
    }


    @GetMapping("/anxiety-hierarchy/{sessionId}")
    @Operation(summary = "2. 불안 위계표 조회", description = "사용자가 선택할 강박상황 목록을 가져옵니다.")
    public Mono<ResponseEntity<List<String>>> getAnxietyHierarchy(
            @Parameter(description = "세션 ID") @PathVariable String sessionId) {
        log.info("불안 위계표 조회 요청 - sessionId: {}", sessionId);
        return preventionService.getAnxietyHierarchy(sessionId)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/situation-input")
    @Operation(summary = "3. 강박상황 입력", description = "위계표에서 선택하거나 직접 입력합니다.")
    public Mono<ResponseEntity<String>> inputSituation(@RequestBody SituationInputRequestDTO request) {
        log.info("강박상황 입력 요청 - sessionId: {}, situation: {}", 
                request.getSessionId(), request.getObsessiveSituation());
        return preventionService.inputSituation(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/thought-input")
    @Operation(summary = "4. 강박사고 입력", description = "강박사고를 직접 입력합니다.")
    public Mono<ResponseEntity<String>> inputThought(@RequestBody ThoughtInputRequestDTO request) {
        log.info("강박사고 입력 요청 - sessionId: {}, thought: {}", 
                request.getSessionId(), request.getObsessiveThought());
        return preventionService.inputThought(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/report")
    @Operation(summary = "5. 반응방지 리포트 생성", description = "입력된 데이터로 반응방지 리포트를 생성합니다.")
    public Mono<ResponseEntity<ReportResponseDTO>> generateReport(@RequestBody ReportRequestDTO request) {
        log.info("반응방지 리포트 생성 요청 - sessionId: {}, duration: {}초", 
                request.getSessionId(), request.getSessionDurationSeconds());
        return preventionService.generateReport(request)
                .map(ResponseEntity::ok);
    }
}
