package com.study.mindit.domain.chat.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.mindit.domain.chat.domain.Chat;
import com.study.mindit.domain.chat.dto.request.ChatRequestDTO_1;
import com.study.mindit.domain.chat.dto.request.ChatRequestDTO_2;
import com.study.mindit.domain.chat.dto.request.ConversationItem;
import com.study.mindit.domain.chat.dto.response.ChatResponseDTO_1;
import com.study.mindit.domain.chat.dto.response.ChatResponseDTO_2;
import com.study.mindit.domain.chat.dto.response.ChatResponseDTO_3;
import com.study.mindit.global.fastApi.FastApiUrls;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FastApiUrls fastApiUrls;

    // analyze1 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_1로 받음)
    public Mono<ChatResponseDTO_1> callAnalyze1(String sessionId, String content) {
        ChatRequestDTO_1 requestBody = ChatRequestDTO_1.builder()
                .content(content)
                .sessionId(sessionId)
                .build();
        return callFastApiEndpoint(fastApiUrls.getAnalyze1(), requestBody, ChatResponseDTO_1.class);
    }

    // analyze2 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_2로 받음)
    public Mono<ChatResponseDTO_2> callAnalyze2(String sessionId, List<Chat> chatHistory) {
        List<ConversationItem> conversation = chatHistory.stream()
                .map(chat -> new ConversationItem(chat.getSender().name().toLowerCase(), chat.getContent()))
                .collect(Collectors.toList());

        ChatRequestDTO_2 requestBody = ChatRequestDTO_2.builder()
                .conversationHistory(conversation)
                .sessionId(sessionId)
                .build();
        return callFastApiEndpoint(fastApiUrls.getAnalyze2(), requestBody, ChatResponseDTO_2.class);
    }

    // analyze3 엔드포인트에 요청 (응답 DTO를 ChatResponseDTO_3으로 받음)
    public Mono<ChatResponseDTO_3> callAnalyze3(String sessionId, List<Chat> chatHistory) {
        List<ConversationItem> conversation = chatHistory.stream()
                .map(chat -> new ConversationItem(chat.getSender().name().toLowerCase(), chat.getContent()))
                .collect(Collectors.toList());

        ChatRequestDTO_2 requestBody = ChatRequestDTO_2.builder()
                .conversationHistory(conversation)
                .sessionId(sessionId)
                .build();
        return callFastApiEndpoint(fastApiUrls.getAnalyze3(), requestBody, ChatResponseDTO_3.class);
    }

    // 요청과 응답 타입을 범용적으로 처리하는 메서드
    private <T> Mono<T> callFastApiEndpoint(String endpointUrl, Object requestBody, Class<T> responseType) {
        String requestBodyJson;
        try {
            requestBodyJson = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("JSON 변환 오류", e));
        }

        return webClientBuilder.build()
                .post()
                .uri(endpointUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(requestBodyJson))
                .retrieve()
                .bodyToMono(responseType) // 응답 타입을 동적으로 지정
                .doOnError(throwable -> System.err.println("API 호출 중 오류 발생: " + throwable.getMessage()))
                .onErrorResume(throwable -> Mono.error(new RuntimeException("API 호출 실패", throwable)));
    }
}