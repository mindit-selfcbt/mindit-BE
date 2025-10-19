package com.study.mindit.domain.chat.presentation;

import com.study.mindit.domain.chat.application.CognitionChatService;
import com.study.mindit.domain.chat.dto.cognition.request.CognitionChatRequestDTO_1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CognitionChatController {

    private final CognitionChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // WebSocket - 인지적 오류 파악하기 메시지 처리
    @MessageMapping("/cognition/{sessionId}")
    public void processCognitionMessage(
            @DestinationVariable String sessionId, 
            @Payload CognitionChatRequestDTO_1 chatRequest) {
        
        log.info("=== Cognition WebSocket 메시지 수신됨! ===");
        log.info("sessionId: {}", sessionId);
        log.info("chatRequest: {}", chatRequest);
        log.info("userText: {}", chatRequest.getUserText());
        
        chatService.processCognitionMessage(chatRequest)
                .doOnNext(cognitionResponse -> {
                    log.info("=== Cognition AI 응답 생성 완료 ===");
                    log.info("cognitionResponse: {}", cognitionResponse);
                })
                .doOnError(error -> {
                    log.error("=== Cognition AI 처리 에러 ===", error);
                })
                .subscribe(cognitionResponse -> {
                    log.info("=== Cognition WebSocket으로 응답 전송 시작 ===");
                    messagingTemplate.convertAndSend("/topic/cognition/" + sessionId, cognitionResponse);
                    log.info("=== Cognition WebSocket 응답 전송 완료 ===");
                });
    }
}
