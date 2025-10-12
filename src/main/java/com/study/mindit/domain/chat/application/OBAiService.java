package com.study.mindit.domain.chat.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.mindit.domain.chat.dto.obsession.request.OBChatRequestDTO_1;
import com.study.mindit.domain.chat.dto.obsession.request.OBChatRequestDTO_2;
import com.study.mindit.domain.chat.dto.obsession.request.OBConversationDTO;
import com.study.mindit.domain.chat.dto.obsession.response.OBChatResponseDTO_1;
import com.study.mindit.domain.chat.dto.obsession.response.OBChatResponseDTO_2;
import com.study.mindit.domain.chat.dto.obsession.response.OBChatResponseDTO_3;
import com.study.mindit.domain.chat.dto.obsession.response.OBChatResponseDTO_4;
import com.study.mindit.domain.chat.dto.obsession.response.OBChatResponseDTO_7;
import com.study.mindit.domain.chat.dto.obsession.response.OBChatResponseDTO_9;
import com.study.mindit.global.fastApi.FastApiUrls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OBAiService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FastApiUrls fastApiUrls;

    // analyze1 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_1로 받음)
    public Mono<OBChatResponseDTO_1> callAnalyze1(String sessionId, Object content) {
        log.info("=== FastAPI analyze1 호출 시작 ===");
        log.info("sessionId: {}, content: {}", sessionId, content);
        
        OBChatRequestDTO_1 requestBody = OBChatRequestDTO_1.builder()
                .content(content)
                .sessionId(sessionId)
                .build();
        
        return callFastApiEndpoint(fastApiUrls.getAnalyze1(), requestBody, OBChatResponseDTO_1.class)
                .doOnNext(response -> log.info("=== FastAPI 응답 수신: {} ===", response))
                .doOnError(error -> log.error("=== FastAPI 호출 에러 ===", error));
    }

    // analyze2 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_2로 받음)
    public Mono<OBChatResponseDTO_2> callAnalyze2(String sessionId, List<OBConversationDTO> conversationHistory) {
        OBChatRequestDTO_2 requestBody = OBChatRequestDTO_2.builder()
                .conversationHistory(conversationHistory)
                .sessionId(sessionId)
                .build();
        return callFastApiEndpoint(fastApiUrls.getAnalyze2(), requestBody, OBChatResponseDTO_2.class);
    }

    // analyze3 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_3으로 받음)
    public Mono<OBChatResponseDTO_3> callAnalyze3(String sessionId, List<OBConversationDTO> conversationHistory) {
        OBChatRequestDTO_2 requestBody = OBChatRequestDTO_2.builder()
                .conversationHistory(conversationHistory)
                .sessionId(sessionId)
                .build();
        return callFastApiEndpoint(fastApiUrls.getAnalyze3(), requestBody, OBChatResponseDTO_3.class);
    }

    // analyze4 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_4로 받음)
    public Mono<OBChatResponseDTO_4> callAnalyze4(String sessionId, List<OBConversationDTO> conversationHistory) {
        OBChatRequestDTO_2 requestBody = OBChatRequestDTO_2.builder()
                .conversationHistory(conversationHistory)
                .sessionId(sessionId)
                .build();
        return callFastApiEndpoint(fastApiUrls.getAnalyze4(), requestBody, OBChatResponseDTO_4.class);
    }

    // analyze5 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_2로 받음)
    public Mono<OBChatResponseDTO_2> callAnalyze5(String sessionId, List<OBConversationDTO> conversationHistory) {
        OBChatRequestDTO_2 requestBody = OBChatRequestDTO_2.builder()
                .conversationHistory(conversationHistory)
                .sessionId(sessionId)
                .build();
        return callFastApiEndpoint(fastApiUrls.getAnalyze5(), requestBody, OBChatResponseDTO_2.class);
    }

    // analyze6 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_2로 받음)
    public Mono<OBChatResponseDTO_2> callAnalyze6(String sessionId, List<OBConversationDTO> conversationHistory) {
        OBChatRequestDTO_2 requestBody = OBChatRequestDTO_2.builder()
                .conversationHistory(conversationHistory)
                .sessionId(sessionId)
                .build();
        return callFastApiEndpoint(fastApiUrls.getAnalyze6(), requestBody, OBChatResponseDTO_2.class);
    }

    // analyze7 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_7로 받음)
    public Mono<OBChatResponseDTO_7> callAnalyze7(String sessionId, List<OBConversationDTO> conversationHistory) {
        OBChatRequestDTO_2 requestBody = OBChatRequestDTO_2.builder()
                .conversationHistory(conversationHistory)
                .sessionId(sessionId)
                .build();
        return callFastApiEndpoint(fastApiUrls.getAnalyze7(), requestBody, OBChatResponseDTO_7.class);
    }

    // analyze8 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_2로 받음)
    public Mono<OBChatResponseDTO_2> callAnalyze8(String sessionId, List<OBConversationDTO> conversationHistory) {
        OBChatRequestDTO_2 requestBody = OBChatRequestDTO_2.builder()
                .conversationHistory(conversationHistory)
                .sessionId(sessionId)
                .build();
        return callFastApiEndpoint(fastApiUrls.getAnalyze8(), requestBody, OBChatResponseDTO_2.class);
    }

    // analyze9 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_9로 받음)
    public Mono<OBChatResponseDTO_9> callAnalyze9(String sessionId, List<OBConversationDTO> conversationHistory) {
        OBChatRequestDTO_2 requestBody = OBChatRequestDTO_2.builder()
                .conversationHistory(conversationHistory)
                .sessionId(sessionId)
                .build();
        return callFastApiEndpoint(fastApiUrls.getAnalyze9(), requestBody, OBChatResponseDTO_9.class);
    }

    // 요청과 응답 타입을 범용적으로 처리하는 메서드
    private <T> Mono<T> callFastApiEndpoint(String endpointUrl, Object requestBody, Class<T> responseType) {
        log.info("=== FastAPI 엔드포인트 호출: {} ===", endpointUrl);
        log.info("요청 바디: {}", requestBody);
        
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
                .uri(endpointUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(requestBodyJson))
                .retrieve()
                .bodyToMono(responseType)
                .doOnNext(response -> log.info("=== FastAPI 응답 성공: {} ===", response))
                .doOnError(throwable -> {
                    log.error("=== API 호출 중 오류 발생 ===", throwable);
                })
                .onErrorResume(throwable -> Mono.error(new RuntimeException("API 호출 실패", throwable)));
    }
}