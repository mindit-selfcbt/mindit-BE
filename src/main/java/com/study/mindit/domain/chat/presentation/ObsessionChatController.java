package com.study.mindit.domain.chat.presentation;

import com.study.mindit.domain.chat.application.ObsessionChatService;
import com.study.mindit.domain.chat.domain.ObsessionChatRoom;
import com.study.mindit.domain.chat.domain.RoomType;
import com.study.mindit.domain.chat.dto.obsession.request.ObsessionChatRequestDTO_1;
import com.study.mindit.domain.chat.dto.obsession.response.ObsessionChatRoomResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ObsessionChatController {

    private final ObsessionChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // WebSocket - 메시지 주고받기
    @MessageMapping("/message/{sessionId}")
    public void processMessage(
            @DestinationVariable String sessionId, @Payload ObsessionChatRequestDTO_1 chatRequest) {
        
        log.info("=== WebSocket 메시지 수신됨! ===");
        log.info("sessionId: {}", sessionId);
        log.info("chatRequest: {}", chatRequest);
        log.info("content: {}", chatRequest.getContent());
        log.info("requestSessionId: {}", chatRequest.getSessionId());
        
        chatService.processChatMessage(chatRequest)
                .doOnNext(aiChat -> {
                    log.info("=== AI 응답 생성 완료 ===");
                    log.info("aiChat: {}", aiChat);
                })
                .doOnError(error -> {
                    log.error("=== AI 처리 에러 ===", error);
                })
                .subscribe(aiChat -> {
                    log.info("=== WebSocket으로 응답 전송 시작 ===");
                    messagingTemplate.convertAndSend("/topic/" + sessionId, aiChat);
                    log.info("=== WebSocket 응답 전송 완료 ===");
                });
    }

    // REST API - 채팅방 생성
    @PostMapping("/room")
    public Mono<ResponseEntity<ObsessionChatRoomResponseDTO>> createChatRoom(
            @RequestParam RoomType roomType) {
        return chatService.createChatRoom(roomType)
                .map(savedRoom -> ResponseEntity.ok(savedRoom));
    }

    // REST API - 채팅방 목록 조회
    @GetMapping("/room")
    public Flux<ObsessionChatRoomResponseDTO> getChatRooms() {
        return chatService.getChatRooms();
    }

    // REST API - 채팅방의 메시지 목록 조회
    @GetMapping("/room/{sessionId}")
    public Mono<ResponseEntity<ObsessionChatRoom>> getChatMessages(@PathVariable String sessionId) {
        return chatService.getChatMessages(sessionId)
                .map(chatRoom -> ResponseEntity.ok(chatRoom))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    // REST API - 채팅방 Step 설정 (테스트용)
    @PutMapping("/room/{sessionId}")
    public Mono<ResponseEntity<java.util.Map<String, Object>>> setStep(
            @PathVariable String sessionId,
            @RequestParam int step) {
        return chatService.setStep(sessionId, step)
                .map(chatRoom -> ResponseEntity.ok(
                        java.util.Map.<String, Object>of(
                                "session_id", chatRoom.getId(),
                                "message", "Step이 " + chatRoom.getCurrentStep() + " 단계로 성공적으로 변경되었습니다."
                        )
                ))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
