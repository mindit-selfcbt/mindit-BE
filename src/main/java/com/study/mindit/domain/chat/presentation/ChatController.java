package com.study.mindit.domain.chat.presentation;

import com.study.mindit.domain.chat.application.ChatService;
import com.study.mindit.domain.chat.domain.RoomType;
import com.study.mindit.domain.chat.dto.request.ChatRequestDTO_1;
import com.study.mindit.domain.chat.dto.response.ChatResponseDTO;
import com.study.mindit.domain.chat.dto.response.ChatRoomResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // WebSocket - 메시지 주고받기
    @MessageMapping("/message/{sessionId}")
    public void processMessage(
            @DestinationVariable String sessionId, @Payload ChatRequestDTO_1 chatRequest) {
        chatService.processChatMessage(chatRequest)
                .subscribe(aiChat -> {
                    messagingTemplate.convertAndSend("/topic/" + sessionId, aiChat);
                });
    }

    // REST API - 채팅방 생성
    @PostMapping("/room")
    public Mono<ResponseEntity<ChatRoomResponseDTO>> createChatRoom(
            @RequestParam RoomType roomType) {
        return chatService.createChatRoom(roomType)
                .map(savedRoom -> ResponseEntity.ok(savedRoom));
    }

    // REST API - 채팅방 목록 조회
    @GetMapping("/room")
    public Flux<ChatRoomResponseDTO> getChatRooms() {
        return chatService.getChatRooms();
    }

    // REST API - 채팅방의 메시지 목록 조회
    @GetMapping("/room/{sessionId}")
    public Flux<ChatResponseDTO> getChatMessages(@PathVariable String sessionId) {
        return chatService.getChatMessages(sessionId);
    }
}
