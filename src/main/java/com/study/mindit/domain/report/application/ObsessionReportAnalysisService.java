package com.study.mindit.domain.report.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.mindit.domain.chat.domain.ObsessionChatRoom;
import com.study.mindit.domain.chat.domain.ObsessionConversation;
import com.study.mindit.domain.chat.domain.repository.ObsessionChatRoomRepository;
import com.study.mindit.domain.report.domain.ObsessionReportAnalysis;
import com.study.mindit.domain.report.domain.repository.ObsessionReportAnalysisRepository;
import com.study.mindit.domain.report.dto.obsession.response.ObsessionReportAnalysisResponseDTO;
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
public class ObsessionReportAnalysisService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FastApiUrls fastApiUrls;
    private final ObsessionChatRoomRepository obsessionChatRoomRepository;
    private final ObsessionReportAnalysisRepository obsessionReportAnalysisRepository;

    /**
     * 세션 ID로 obsession 채팅 결과를 조회하여 레포트 분석을 생성합니다.
     * 
     * @param sessionId 세션 ID
     * @return 생성된 레포트 분석 정보
     */
    public Mono<ObsessionReportAnalysisResponseDTO> generateReportAnalysis(String sessionId) {
        log.info("=== 레포트 분석 생성 시작 ===");
        log.info("세션 ID: {}", sessionId);
        
        return obsessionChatRoomRepository.findById(sessionId)
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom을 찾을 수 없습니다: " + sessionId)))
                .flatMap(chatRoom -> {
                    log.info("=== ChatRoom 조회 완료. 대화 기록 수: {} ===", chatRoom.getConversationHistory().size());
                    return callFastApiForReportAnalysis(chatRoom);
                })
                .flatMap(this::saveReportAnalysisToDatabase)
                .doOnNext(response -> log.info("=== 레포트 분석 생성 및 저장 완료: {} ===", response))
                .doOnError(error -> log.error("=== 레포트 분석 생성 실패 ===", error));
    }

    /**
     * FastAPI의 analyzeReport 엔드포인트를 호출하여 레포트 분석을 생성합니다.
     */
    private Mono<ObsessionReportAnalysisResponseDTO> callFastApiForReportAnalysis(ObsessionChatRoom chatRoom) {
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
                .bodyToMono(ObsessionReportAnalysisResponseDTO.class)
                .doOnNext(response -> log.info("=== FastAPI 레포트 분석 응답 성공: {} ===", response))
                .doOnError(throwable -> {
                    log.error("=== 레포트 분석 API 호출 중 오류 발생 ===", throwable);
                })
                .onErrorResume(throwable -> Mono.error(new RuntimeException("레포트 분석 API 호출 실패", throwable)));
    }

    /**
     * 레포트 분석을 데이터베이스에 저장합니다.
     */
    private Mono<ObsessionReportAnalysisResponseDTO> saveReportAnalysisToDatabase(ObsessionReportAnalysisResponseDTO reportResponse) {
        log.info("=== 레포트 분석 DB 저장 시작 ===");
        
        ObsessionReportAnalysis reportAnalysis = convertResponseToReportAnalysis(reportResponse);
        
        return obsessionReportAnalysisRepository.save(reportAnalysis)
                .doOnNext(savedReport -> log.info("=== 레포트 분석 DB 저장 완료: {} ===", savedReport.getId()))
                .thenReturn(reportResponse)
                .doOnError(error -> log.error("=== 레포트 분석 DB 저장 실패 ===", error));
    }

    /**
     * ObsessionReportAnalysisResponseDTO를 ObsessionReportAnalysis 도메인으로 변환합니다.
     */
    private ObsessionReportAnalysis convertResponseToReportAnalysis(ObsessionReportAnalysisResponseDTO response) {
        return ObsessionReportAnalysis.builder()
                .sessionId(response.getSessionId())
                .obsessionType(response.getObsessionType())
                .anxietyThoughts(response.getAnxietyThoughts())
                .compulsiveBehaviors(response.getCompulsiveBehaviors())
                .anxietyHierarchy(convertAnxietyHierarchy(response.getAnxietyHierarchy()))
                .build();
    }

    /**
     * AnxietyHierarchy 리스트를 변환합니다.
     */
    private List<ObsessionReportAnalysis.AnxietyHierarchy> convertAnxietyHierarchy(List<ObsessionReportAnalysisResponseDTO.AnxietyHierarchy> hierarchy) {
        if (hierarchy == null) return null;
        
        return hierarchy.stream()
                .map(item -> ObsessionReportAnalysis.AnxietyHierarchy.builder()
                        .situation(item.getSituation())
                        .score(item.getScore())
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
