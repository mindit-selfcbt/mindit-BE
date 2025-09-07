package com.study.mindit.domain.chat.application;

import com.study.mindit.domain.chat.domain.Chat;
import com.study.mindit.domain.chat.domain.ChatRoom;
import com.study.mindit.domain.chat.domain.SenderType;
import com.study.mindit.domain.chat.domain.repository.ChatRepository;
import com.study.mindit.domain.chat.domain.repository.ChatRoomRepository;
import com.study.mindit.domain.chat.dto.ChatRequestDto;
import com.study.mindit.domain.chat.dto.ChatResponseDto;
import com.study.mindit.domain.chat.dto.ChatRoomRequestDto;
import com.study.mindit.domain.chat.dto.ChatRoomResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AiService aiService;

    // 클라이언트의 메시지를 받아 전체 대화 흐름을 처리하는 핵심 메서드
    public Mono<ChatResponseDto> processChatMessage(ChatRequestDto chatRequestDto) {
        Chat incompleteUserChat = chatRequestDto.to();

        return chatRepository.findByRoomId(incompleteUserChat.getRoomId())
                .collectList()
                .flatMap(chatHistory -> {
                    int currentStep = getLastMessageStep(chatHistory);

                    Chat savedUserChat = Chat.builder()
                            .sender(SenderType.USER)
                            .content(incompleteUserChat.getContent())
                            .roomId(incompleteUserChat.getRoomId())
                            .step(currentStep + 1)
                            .build();

                    return chatRepository.save(savedUserChat)
                            .then(callFastApiBasedOnStep(currentStep + 1, chatHistory, savedUserChat))
                            .flatMap(aiResponse -> {
                                Chat aiChat = Chat.builder()
                                        .sender(SenderType.AI)
                                        .content(aiResponse)
                                        .roomId(incompleteUserChat.getRoomId())
                                        .step(currentStep + 1)
                                        .build();

                                return chatRepository.save(aiChat)
                                        .map(savedAiChat -> ChatResponseDto.from(savedAiChat));
                            });
                });
    }

    // step 번호에 따라 다른 FastAPI API를 호출하는 라우터 역할의 메서드
    private Mono<String> callFastApiBasedOnStep(int step, List<Chat> chatHistory, Chat userChat) {
        switch (step) {
            case 1:
                return aiService.callAnalyze1(chatHistory, userChat.getContent());
            case 2:
                return aiService.callAnalyze2(chatHistory, userChat.getContent());
            case 3:
                return aiService.callAnalyze3(chatHistory, userChat.getContent());
            default:
                return Mono.error(new IllegalArgumentException("잘못된 대화 단계입니다: " + step));
        }
    }

    // 이전 대화 기록에서 AI의 마지막 메시지 step을 찾는 헬퍼 메서드
    private int getLastMessageStep(List<Chat> chatHistory) {
        if (chatHistory == null || chatHistory.isEmpty()) {
            return 0;
        }
        return chatHistory.stream()
                .filter(chat -> "ai".equals(chat.getSender()))
                .mapToInt(Chat::getStep)
                .max()
                .orElse(0);
    }

    /**
     * 채팅방을 생성하는 메서드
     */
    public Mono<ChatRoomResponseDto> createChatRoom(ChatRoomRequestDto chatRoomRequestDto) {
        ChatRoom chatRoom = chatRoomRequestDto.to();
        return chatRoomRepository.save(chatRoom)
                .map(savedRoom -> ChatRoomResponseDto.from(savedRoom));
    }

    /**
     * 모든 채팅방 목록을 조회하는 메서드
     */
    public Flux<ChatRoomResponseDto> getChatRooms() {
        return chatRoomRepository.findAll()
                .map(chatRoom -> ChatRoomResponseDto.from(chatRoom));
    }

    /**
     * 특정 채팅방의 메시지 목록을 조회하는 메서드
     */
    public Flux<ChatResponseDto> getChatMessages(String roomId) {
        return chatRepository.findByRoomId(roomId)
                .map(chat -> ChatResponseDto.from(chat));
    }
}