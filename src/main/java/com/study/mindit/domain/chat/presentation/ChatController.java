package com.study.mindit.domain.chat.presentation;

import com.study.mindit.domain.chat.application.ChatService;
import com.study.mindit.domain.chat.domain.Chat;
import com.study.mindit.domain.chat.dto.ChatRequestDto;
import com.study.mindit.domain.chat.dto.ChatResponseDto;
import com.study.mindit.domain.chat.dto.ChatRoomRequestDto;
import com.study.mindit.domain.chat.dto.ChatRoomResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // WebSocket 메시지
    @MessageMapping("/message/{roomId}")
    public void processMessage(@DestinationVariable String roomId, @Payload ChatRequestDto chatRequest) {
        chatService.processChatMessage(chatRequest)
                .subscribe(aiChat -> {
                    messagingTemplate.convertAndSend("/topic/" + roomId, aiChat);
                });
    }

    // REST API - 채팅방 생성
    @PostMapping("/room")
    public Mono<ResponseEntity<ChatRoomResponseDto>> createChatRoom(@RequestBody ChatRoomRequestDto chatRoomRequestDto) {
        return chatService.createChatRoom(chatRoomRequestDto)
                .map(savedRoom -> ResponseEntity.ok(savedRoom));
    }

    // REST API - 채팅방 목록 조회
    @GetMapping("/room")
    public Flux<ChatRoomResponseDto> getChatRooms() {
        return chatService.getChatRooms();
    }

    // REST API - 채팅방의 메시지 목록 조회
    @GetMapping("/room/{roomId}")
    public Flux<ChatResponseDto> getChatMessages(@PathVariable String roomId) {
        return chatService.getChatMessages(roomId);
    }
}
