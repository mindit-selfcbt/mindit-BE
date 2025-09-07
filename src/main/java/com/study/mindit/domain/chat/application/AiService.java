package com.study.mindit.domain.chat.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.study.mindit.domain.chat.domain.Chat;
import com.study.mindit.global.fastApi.FastApiUrls;
import reactor.core.publisher.Mono;
import java.util.List;

/**
 * AI 서비스 클래스
 * FastAPI 서버와 통신하여 AI 응답을 받아오는 역할을 담당
 */
@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FastApiUrls fastApiUrls;

    /**
     * 첫 번째 대화 단계 분석을 위한 AI 응답 요청
     */
    public Mono<String> callAnalyze1(List<Chat> chatHistory, String userMessage) {
        return callFastApiEndpoint(fastApiUrls.getAnalyze1(), chatHistory, userMessage);
    }

    /**
     * 두 번째 대화 단계 분석을 위한 AI 응답 요청
     */
    public Mono<String> callAnalyze2(List<Chat> chatHistory, String userMessage) {
        return callFastApiEndpoint(fastApiUrls.getAnalyze2(), chatHistory, userMessage);
    }

    /**
     * 세 번째 대화 단계 분석을 위한 AI 응답 요청
     */
    public Mono<String> callAnalyze3(List<Chat> chatHistory, String userMessage) {
        return callFastApiEndpoint(fastApiUrls.getAnalyze3(), chatHistory, userMessage);
    }

    /**
     * 네 번째 대화 단계 분석을 위한 AI 응답 요청
     */
    public Mono<String> callAnalyze4(List<Chat> chatHistory, String userMessage) {
        return callFastApiEndpoint(fastApiUrls.getAnalyze4(), chatHistory, userMessage);
    }

    /**
     * 공통 FastAPI 엔드포인트 호출 메서드
     */
    private Mono<String> callFastApiEndpoint(String endpointUrl, List<Chat> chatHistory, String userMessage) {
        try {
            String chatHistoryJson = objectMapper.writeValueAsString(chatHistory);
            String requestBody = String.format("{\"chat_history\": %s, \"user_message\": \"%s\"}", chatHistoryJson, userMessage);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    endpointUrl,
                    entity,
                    String.class
            );

            return Mono.just(response.getBody());

        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}
