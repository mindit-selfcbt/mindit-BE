package com.study.mindit.domain.report.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.mindit.domain.chat.domain.ObsessionChatRoom;
import com.study.mindit.domain.chat.domain.ObsessionConversation;
import com.study.mindit.domain.chat.domain.repository.ObsessionChatRoomRepository;
import com.study.mindit.domain.report.domain.*;
import com.study.mindit.domain.report.domain.repository.ObsessionPreventionPlanRepository;
import com.study.mindit.domain.report.dto.obsession.response.ObsessionPreventionPlanResponseDTO;
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
public class ObsessionPreventionPlanService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FastApiUrls fastApiUrls;
    private final ObsessionChatRoomRepository obsessionChatRoomRepository;
    private final ObsessionPreventionPlanRepository obsessionPreventionPlanRepository;

    /**
     * 세션 ID로 obsession 채팅 결과를 조회하여 예방 계획을 생성합니다.
     * 
     * @param sessionId 세션 ID
     * @return 생성된 예방 계획 정보
     */
    public Mono<ObsessionPreventionPlanResponseDTO> generatePreventionPlan(String sessionId) {
        log.info("=== 예방 계획 생성 시작 ===");
        log.info("세션 ID: {}", sessionId);
        
        return obsessionChatRoomRepository.findById(sessionId)
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom을 찾을 수 없습니다: " + sessionId)))
                .flatMap(chatRoom -> {
                    log.info("=== ChatRoom 조회 완료. 대화 기록 수: {} ===", chatRoom.getConversationHistory().size());
                    return callFastApiForPreventionPlan(chatRoom);
                })
                .flatMap(this::savePreventionPlanToDatabase)
                .doOnNext(response -> log.info("=== 예방 계획 생성 및 저장 완료: {} ===", response))
                .doOnError(error -> log.error("=== 예방 계획 생성 실패 ===", error));
    }

    /**
     * FastAPI의 analyzeReport2 엔드포인트를 호출하여 예방 계획을 생성합니다.
     */
    private Mono<ObsessionPreventionPlanResponseDTO> callFastApiForPreventionPlan(ObsessionChatRoom chatRoom) {
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
                .bodyToMono(ObsessionPreventionPlanResponseDTO.class)
                .doOnNext(response -> log.info("=== FastAPI 예방 계획 응답 성공: {} ===", response))
                .doOnError(throwable -> {
                    log.error("=== 예방 계획 API 호출 중 오류 발생 ===", throwable);
                })
                .onErrorResume(throwable -> Mono.error(new RuntimeException("예방 계획 API 호출 실패", throwable)));
    }

    /**
     * 예방 계획을 데이터베이스에 저장합니다.
     */
    private Mono<ObsessionPreventionPlanResponseDTO> savePreventionPlanToDatabase(ObsessionPreventionPlanResponseDTO planResponse) {
        log.info("=== 예방 계획 DB 저장 시작 ===");
        
        ObsessionPreventionPlan preventionPlan = convertResponseToPreventionPlan(planResponse);
        
        return obsessionPreventionPlanRepository.save(preventionPlan)
                .doOnNext(savedPlan -> log.info("=== 예방 계획 DB 저장 완료: {} ===", savedPlan.getId()))
                .thenReturn(planResponse)
                .doOnError(error -> log.error("=== 예방 계획 DB 저장 실패 ===", error));
    }

    /**
     * ObsessionPreventionPlanResponseDTO를 ObsessionPreventionPlan 도메인으로 변환합니다.
     */
    private ObsessionPreventionPlan convertResponseToPreventionPlan(ObsessionPreventionPlanResponseDTO response) {
        return ObsessionPreventionPlan.builder()
                .sessionId(response.getSessionId())
                .obsessionType(response.getObsessionType())
                .trainingExplanation(convertTrainingExplanation(response.getTrainingExplanation()))
                .weeks(convertWeeks(response.getWeeks()))
                .trainingPlan(convertTrainingPlan(response.getTrainingPlan()))
                .build();
    }

    /**
     * TrainingExplanation을 변환합니다.
     */
    private TrainingExplanation convertTrainingExplanation(ObsessionPreventionPlanResponseDTO.TrainingExplanation explanation) {
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
    private TrainingMethod convertTrainingMethod(ObsessionPreventionPlanResponseDTO.TrainingMethod method) {
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
    private List<WeekInfo> convertWeeks(List<ObsessionPreventionPlanResponseDTO.WeekInfo> weeks) {
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
    private List<TrainingPlan> convertTrainingPlan(List<ObsessionPreventionPlanResponseDTO.TrainingPlan> plans) {
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

