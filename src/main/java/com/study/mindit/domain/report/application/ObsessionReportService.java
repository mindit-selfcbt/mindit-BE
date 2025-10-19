package com.study.mindit.domain.report.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.mindit.domain.chat.domain.ObsessionChatRoom;
import com.study.mindit.domain.chat.domain.ObsessionConversation;
import com.study.mindit.domain.chat.domain.repository.ObsessionChatRoomRepository;
import com.study.mindit.domain.report.domain.*;
import com.study.mindit.domain.report.domain.repository.ObsessionReportRepository;
import com.study.mindit.domain.report.dto.obsession.request.ObsessionReportRequestDTO;
import com.study.mindit.domain.report.dto.obsession.response.ObsessionReportResponseDTO;
import com.study.mindit.global.fastApi.FastApiUrls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObsessionReportService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FastApiUrls fastApiUrls;
    private final ObsessionChatRoomRepository obsessionChatRoomRepository;
    private final ObsessionReportRepository obsessionReportRepository;

    /**
     * 세션 ID로 obsession 채팅 결과를 조회하여 레포트를 생성합니다.
     * 
     * @param reportRequest 세션 ID가 포함된 요청
     * @return 생성된 레포트 정보
     */
    public Mono<ObsessionReportResponseDTO> generateReport(ObsessionReportRequestDTO reportRequest) {
        log.info("=== 레포트 생성 시작 ===");
        log.info("세션 ID: {}", reportRequest.getSessionId());
        
        return obsessionChatRoomRepository.findById(reportRequest.getSessionId())
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom을 찾을 수 없습니다: " + reportRequest.getSessionId())))
                .flatMap(chatRoom -> {
                    log.info("=== ChatRoom 조회 완료. 대화 기록 수: {} ===", chatRoom.getConversationHistory().size());
                    return callFastApiForReport(chatRoom);
                })
                .flatMap(this::saveReportToDatabase)
                .doOnNext(response -> log.info("=== 레포트 생성 및 저장 완료: {} ===", response))
                .doOnError(error -> log.error("=== 레포트 생성 실패 ===", error));
    }

    /**
     * FastAPI의 analyzeReport 엔드포인트를 호출하여 레포트를 생성합니다.
     */
    private Mono<ObsessionReportResponseDTO> callFastApiForReport(ObsessionChatRoom chatRoom) {
        log.info("=== FastAPI analyzeReport 호출 시작 ===");
        
        // 대화 기록을 FastAPI가 요구하는 형식으로 변환
        List<Map<String, Object>> conversationHistory = chatRoom.getConversationHistory().stream()
                .map(this::convertConversationToMap)
                .toList();
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("conversation_history", conversationHistory);
        requestBody.put("session_id", chatRoom.getId());
        
        String requestBodyJson;
        try {
            requestBodyJson = objectMapper.writeValueAsString(requestBody);
            log.info("JSON 변환 완료: {}", requestBodyJson);
        } catch (JsonProcessingException e) {
            log.error("=== JSON 변환 오류 ===", e);
            return Mono.error(new RuntimeException("JSON 변환 오류", e));
        }

        return webClientBuilder.build()
                .post()
                .uri(fastApiUrls.getAnalyzeReport())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(requestBodyJson))
                .retrieve()
                .bodyToMono(ObsessionReportResponseDTO.class)
                .doOnNext(response -> log.info("=== FastAPI 레포트 응답 성공: {} ===", response))
                .doOnError(throwable -> {
                    log.error("=== 레포트 API 호출 중 오류 발생 ===", throwable);
                })
                .onErrorResume(throwable -> Mono.error(new RuntimeException("레포트 API 호출 실패", throwable)));
    }

    /**
     * 세션 ID로 obsession 채팅 결과를 조회하여 레포트2를 생성합니다.
     * 
     * @param reportRequest 세션 ID가 포함된 요청
     * @return 생성된 레포트2 정보
     */
    public Mono<ObsessionReportResponseDTO> generateReport2(ObsessionReportRequestDTO reportRequest) {
        log.info("=== 레포트2 생성 시작 ===");
        log.info("세션 ID: {}", reportRequest.getSessionId());
        
        return obsessionChatRoomRepository.findById(reportRequest.getSessionId())
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom을 찾을 수 없습니다: " + reportRequest.getSessionId())))
                .flatMap(chatRoom -> {
                    log.info("=== ChatRoom 조회 완료. 대화 기록 수: {} ===", chatRoom.getConversationHistory().size());
                    return callFastApiForReport2(chatRoom);
                })
                .flatMap(this::saveReport2ToDatabase)
                .doOnNext(response -> log.info("=== 레포트2 생성 및 저장 완료: {} ===", response))
                .doOnError(error -> log.error("=== 레포트2 생성 실패 ===", error));
    }

    /**
     * FastAPI의 analyzeReport2 엔드포인트를 호출하여 레포트2를 생성합니다.
     */
    private Mono<ObsessionReportResponseDTO> callFastApiForReport2(ObsessionChatRoom chatRoom) {
        log.info("=== FastAPI analyzeReport2 호출 시작 ===");
        
        // 대화 기록을 FastAPI가 요구하는 형식으로 변환
        List<Map<String, Object>> conversationHistory = chatRoom.getConversationHistory().stream()
                .map(this::convertConversationToMap)
                .toList();
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("conversation_history", conversationHistory);
        requestBody.put("session_id", chatRoom.getId());
        
        String requestBodyJson;
        try {
            requestBodyJson = objectMapper.writeValueAsString(requestBody);
            log.info("JSON 변환 완료: {}", requestBodyJson);
        } catch (JsonProcessingException e) {
            log.error("=== JSON 변환 오류 ===", e);
            return Mono.error(new RuntimeException("JSON 변환 오류", e));
        }

        return webClientBuilder.build()
                .post()
                .uri(fastApiUrls.getAnalyzeReport2())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(requestBodyJson))
                .retrieve()
                .bodyToMono(ObsessionReportResponseDTO.class)
                .doOnNext(response -> log.info("=== FastAPI 레포트2 응답 성공: {} ===", response))
                .doOnError(throwable -> {
                    log.error("=== 레포트2 API 호출 중 오류 발생 ===", throwable);
                })
                .onErrorResume(throwable -> Mono.error(new RuntimeException("레포트2 API 호출 실패", throwable)));
    }

    /**
     * 레포트1을 데이터베이스에 저장합니다.
     */
    private Mono<ObsessionReportResponseDTO> saveReportToDatabase(ObsessionReportResponseDTO reportResponse) {
        log.info("=== 레포트1 DB 저장 시작 ===");
        
        ObsessionReport report = convertResponseToReport(reportResponse, ReportType.REPORT_1);
        
        return obsessionReportRepository.save(report)
                .doOnNext(savedReport -> log.info("=== 레포트1 DB 저장 완료: {} ===", savedReport.getId()))
                .thenReturn(reportResponse)
                .doOnError(error -> log.error("=== 레포트1 DB 저장 실패 ===", error));
    }

    /**
     * 레포트2를 데이터베이스에 저장합니다.
     */
    private Mono<ObsessionReportResponseDTO> saveReport2ToDatabase(ObsessionReportResponseDTO reportResponse) {
        log.info("=== 레포트2 DB 저장 시작 ===");
        
        ObsessionReport report = convertResponseToReport(reportResponse, ReportType.REPORT_2);
        
        return obsessionReportRepository.save(report)
                .doOnNext(savedReport -> log.info("=== 레포트2 DB 저장 완료: {} ===", savedReport.getId()))
                .thenReturn(reportResponse)
                .doOnError(error -> log.error("=== 레포트2 DB 저장 실패 ===", error));
    }

    /**
     * ObsessionReportResponseDTO를 ObsessionReport 도메인으로 변환합니다.
     */
    private ObsessionReport convertResponseToReport(ObsessionReportResponseDTO response, ReportType reportType) {
        return ObsessionReport.builder()
                .sessionId(response.getSessionId())
                .obsessionType(response.getObsessionType())
                .trainingExplanation(convertTrainingExplanation(response.getTrainingExplanation()))
                .weeks(convertWeeks(response.getWeeks()))
                .trainingPlan(convertTrainingPlan(response.getTrainingPlan()))
                .reportType(reportType)
                .build();
    }

    /**
     * TrainingExplanation을 변환합니다.
     */
    private TrainingExplanation convertTrainingExplanation(ObsessionReportResponseDTO.TrainingExplanation explanation) {
        if (explanation == null) return null;
        
        return TrainingExplanation.builder()
                .responsePrevention(convertTrainingMethod(explanation.getResponsePrevention()))
                .aiArTraining(convertTrainingMethod(explanation.getAiArTraining()))
                .imaginationTraining(convertTrainingMethod(explanation.getImaginationTraining()))
                .build();
    }

    /**
     * TrainingMethod를 변환합니다.
     */
    private TrainingMethod convertTrainingMethod(ObsessionReportResponseDTO.TrainingMethod method) {
        if (method == null) return null;
        
        return TrainingMethod.builder()
                .title(method.getTitle())
                .description(method.getDescription())
                .when(method.getWhen())
                .instruction(method.getInstruction())
                .build();
    }

    /**
     * WeekInfo 리스트를 변환합니다.
     */
    private List<WeekInfo> convertWeeks(List<ObsessionReportResponseDTO.WeekInfo> weeks) {
        if (weeks == null) return null;
        
        return weeks.stream()
                .map(week -> WeekInfo.builder()
                        .week(week.getWeek())
                        .startDate(week.getStartDate())
                        .endDate(week.getEndDate())
                        .build())
                .toList();
    }

    /**
     * TrainingPlan 리스트를 변환합니다.
     */
    private List<TrainingPlan> convertTrainingPlan(List<ObsessionReportResponseDTO.TrainingPlan> plans) {
        if (plans == null) return null;
        
        return plans.stream()
                .map(plan -> TrainingPlan.builder()
                        .week(plan.getWeek())
                        .trainingName(plan.getTrainingName())
                        .goal(plan.getGoal())
                        .content(plan.getContent())
                        .build())
                .toList();
    }

    /**
     * ObsessionConversation을 Map으로 변환합니다.
     */
    private Map<String, Object> convertConversationToMap(ObsessionConversation conversation) {
        Map<String, Object> conversationMap = new HashMap<>();
        conversationMap.put("role", conversation.getRole());
        conversationMap.put("content", conversation.getContent());
        return conversationMap;
    }
}
