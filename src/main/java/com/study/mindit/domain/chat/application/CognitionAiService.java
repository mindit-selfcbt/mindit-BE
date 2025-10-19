package com.study.mindit.domain.chat.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.mindit.domain.chat.dto.cognition.request.CognitionChatRequestDTO_1;
import com.study.mindit.domain.chat.dto.cognition.request.CognitionConversationDTO;
import com.study.mindit.domain.chat.dto.cognition.response.CognitionChatResponseDTO_1;
import com.study.mindit.domain.chat.dto.cognition.response.CognitionChatResponseDTO_3;
import com.study.mindit.domain.chat.dto.cognition.response.CognitionChatResponseDTO_2;
import com.study.mindit.domain.chat.dto.cognition.response.CognitionChatResponseDTO_4;
import com.study.mindit.domain.chat.dto.cognition.response.CognitionChatResponseDTO_5;
import com.study.mindit.domain.chat.dto.cognition.response.CognitionChatResponseDTO_7;
import com.study.mindit.global.fastApi.FastApiUrls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CognitionAiService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FastApiUrls fastApiUrls;

    // Step 1: 인지적 오류 파악하기 도입문 생성 (process_chat_1)
    public Mono<CognitionChatResponseDTO_1> callProcessChat1(String sessionId, String obsessionSituation, 
                                                            Integer anxietyLevelStart, Integer anxietyLevelEnd, 
                                                            String obsessionThought) {
        log.info("=== FastAPI Cognition process_chat_1 호출 시작 ===");
        log.info("sessionId: {}, obsessionSituation: {}, anxietyLevelStart: {}, anxietyLevelEnd: {}, obsessionThought: {}", 
                sessionId, obsessionSituation, anxietyLevelStart, anxietyLevelEnd, obsessionThought);
        
        // 요청 데이터를 Map으로 구성
        java.util.Map<String, Object> requestData = java.util.Map.of(
            "session_id", sessionId,
            "obsession_situation", obsessionSituation,
            "anxiety_level_start", anxietyLevelStart,
            "anxiety_level_end", anxietyLevelEnd,
            "obsession_thought", obsessionThought
        );
        
        return callFastApiEndpoint(fastApiUrls.getProcessChat1(), requestData, CognitionChatResponseDTO_1.class)
                .doOnNext(response -> log.info("=== FastAPI Cognition process_chat_1 응답 수신: {} ===", response))
                .doOnError(error -> log.error("=== FastAPI Cognition process_chat_1 호출 에러 ===", error));
    }

    // Step 2: conversation_history 조회 (process_chat_2)
    public Mono<CognitionChatResponseDTO_1> callProcessChat2(String sessionId, String obsessionType, 
                                                            List<CognitionConversationDTO> conversationHistory) {
        log.info("=== FastAPI Cognition process_chat_2 호출 시작 ===");
        log.info("sessionId: {}, obsessionType: {}", sessionId, obsessionType);
        
        // 요청 데이터를 Map으로 구성
        java.util.Map<String, Object> requestData = java.util.Map.of(
            "session_id", sessionId,
            "obsession_type", obsessionType,
            "conversation_history", conversationHistory
        );
        
        return callFastApiEndpoint(fastApiUrls.getProcessChat2(), requestData, CognitionChatResponseDTO_1.class)
                .doOnNext(response -> log.info("=== FastAPI Cognition process_chat_2 응답 수신: {} ===", response))
                .doOnError(error -> log.error("=== FastAPI Cognition process_chat_2 호출 에러 ===", error));
    }

    // Step 3: 확인강박 분석 (process_chat_3)
    public Mono<CognitionChatResponseDTO_3> callProcessChat3(String sessionId, String obsessionType, 
                                                            List<CognitionConversationDTO> conversationHistory) {
        log.info("=== FastAPI Cognition process_chat_3 호출 시작 ===");
        log.info("sessionId: {}, obsessionType: {}", sessionId, obsessionType);
        
        // 요청 데이터를 Map으로 구성
        java.util.Map<String, Object> requestData = java.util.Map.of(
            "session_id", sessionId,
            "obsession_type", obsessionType,
            "conversation_history", conversationHistory
        );
        
        return callFastApiEndpoint(fastApiUrls.getProcessChat3(), requestData, CognitionChatResponseDTO_3.class)
                .doOnNext(response -> log.info("=== FastAPI Cognition process_chat_3 응답 수신: {} ===", response))
                .doOnError(error -> log.error("=== FastAPI Cognition process_chat_3 호출 에러 ===", error));
    }

    // Step 3: 오염강박 분석 (process_chat_4)
    public Mono<CognitionChatResponseDTO_4> callProcessChat4(String sessionId, String obsessionType, 
                                                            List<CognitionConversationDTO> conversationHistory) {
        log.info("=== FastAPI Cognition process_chat_4 호출 시작 ===");
        log.info("sessionId: {}, obsessionType: {}", sessionId, obsessionType);
        
        // 요청 데이터를 Map으로 구성
        java.util.Map<String, Object> requestData = java.util.Map.of(
            "session_id", sessionId,
            "obsession_type", obsessionType,
            "conversation_history", conversationHistory
        );
        
        return callFastApiEndpoint(fastApiUrls.getProcessChat4(), requestData, CognitionChatResponseDTO_4.class)
                .doOnNext(response -> log.info("=== FastAPI Cognition process_chat_4 응답 수신: {} ===", response))
                .doOnError(error -> log.error("=== FastAPI Cognition process_chat_4 호출 에러 ===", error));
    }

    // Step 4: 오염강박 인지적 재구성 (process_chat_5)
    public Mono<CognitionChatResponseDTO_5> callProcessChat5(String sessionId, String obsessionType, 
                                                            List<CognitionConversationDTO> conversationHistory) {
        log.info("=== FastAPI Cognition process_chat_5 호출 시작 ===");
        log.info("sessionId: {}, obsessionType: {}", sessionId, obsessionType);
        
        // 요청 데이터를 Map으로 구성
        java.util.Map<String, Object> requestData = java.util.Map.of(
            "session_id", sessionId,
            "obsession_type", obsessionType,
            "conversation_history", conversationHistory
        );
        
        return callFastApiEndpoint(fastApiUrls.getProcessChat5(), requestData, CognitionChatResponseDTO_5.class)
                .doOnNext(response -> log.info("=== FastAPI Cognition process_chat_5 응답 수신: {} ===", response))
                .doOnError(error -> log.error("=== FastAPI Cognition process_chat_5 호출 에러 ===", error));
    }

    // Step 4/5: 대화 계속 (process_chat_6)
    public Mono<CognitionChatResponseDTO_4> callProcessChat6(String sessionId, String obsessionType, 
                                                            List<CognitionConversationDTO> conversationHistory) {
        log.info("=== FastAPI Cognition process_chat_6 호출 시작 ===");
        log.info("sessionId: {}, obsessionType: {}", sessionId, obsessionType);
        
        // 요청 데이터를 Map으로 구성
        java.util.Map<String, Object> requestData = java.util.Map.of(
            "session_id", sessionId,
            "obsession_type", obsessionType,
            "conversation_history", conversationHistory
        );
        
        return callFastApiEndpoint(fastApiUrls.getProcessChat6(), requestData, CognitionChatResponseDTO_4.class)
                .doOnNext(response -> log.info("=== FastAPI Cognition process_chat_6 응답 수신: {} ===", response))
                .doOnError(error -> log.error("=== FastAPI Cognition process_chat_6 호출 에러 ===", error));
    }

    // Step 4/5: 대화 마무리 (process_chat_7)
    public Mono<CognitionChatResponseDTO_7> callProcessChat7(String sessionId, String obsessionType, 
                                                            List<CognitionConversationDTO> conversationHistory) {
        log.info("=== FastAPI Cognition process_chat_7 호출 시작 ===");
        log.info("sessionId: {}, obsessionType: {}", sessionId, obsessionType);
        
        // 요청 데이터를 Map으로 구성
        java.util.Map<String, Object> requestData = java.util.Map.of(
            "session_id", sessionId,
            "obsession_type", obsessionType,
            "conversation_history", conversationHistory
        );
        
        return callFastApiEndpoint(fastApiUrls.getProcessChat7(), requestData, CognitionChatResponseDTO_7.class)
                .doOnNext(response -> log.info("=== FastAPI Cognition process_chat_7 응답 수신: {} ===", response))
                .doOnError(error -> log.error("=== FastAPI Cognition process_chat_7 호출 에러 ===", error));
    }

    // 공통 FastAPI 호출 메서드
    private <T> Mono<T> callFastApiEndpoint(String url, Object requestBody, Class<T> responseType) {
        return webClientBuilder.build()
                .post()
                .uri(url)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType);
    }
}
