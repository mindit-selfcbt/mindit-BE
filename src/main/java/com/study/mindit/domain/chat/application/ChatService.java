package com.study.mindit.domain.chat.application;

import com.study.mindit.domain.chat.domain.Chat;
import com.study.mindit.domain.chat.domain.ChatRoom;
import com.study.mindit.domain.chat.domain.RoomType;
import com.study.mindit.domain.chat.domain.SenderType;
import com.study.mindit.domain.chat.domain.repository.ChatRepository;
import com.study.mindit.domain.chat.domain.repository.ChatRoomRepository;
import com.study.mindit.domain.chat.dto.request.ChatRequestDTO_1;
import com.study.mindit.domain.chat.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AiService aiService;

    // 1. 전체 로직의 시작점이자 흐름을 조정하는 역할
    public Mono<ChatResponseDTO> processChatMessage(ChatRequestDTO_1 chatRequestDto) {
        return chatRepository.findBySessionId(chatRequestDto.getSessionId())
                .collectList()
                .flatMap(chatHistory -> {
                    int nextStep = getLastMessageStep(chatHistory) + 1;
                    Chat userChat = Chat.builder()
                            .sender(SenderType.USER)
                            .content(chatRequestDto.getContent())
                            .sessionId(chatRequestDto.getSessionId())
                            .step(nextStep)
                            .build();

                    return saveUserChatAndCallAi(userChat, chatHistory)
                            .flatMap(aiResponseDto -> saveAiChatAndBuildResponse(userChat, aiResponseDto));
                });
    }

    // 2. 사용자 메시지를 저장하고 AI 서비스를 호출하는 역할
    private Mono<Object> saveUserChatAndCallAi(Chat userChat, List<Chat> chatHistory) {
        return chatRepository.save(userChat)
                .flatMap(savedUserChat -> {
                    // 이 메서드는 단지 다음 메서드로 필요한 인자를 넘겨주는 역할만 합니다.
                    // updatedChatHistory 생성 로직은 제거합니다.
                    return callFastApiBasedOnStep(savedUserChat.getStep(), savedUserChat.getSessionId(), chatHistory, savedUserChat);
                });
    }

    // 3. AI 응답 DTO에서 필요한 콘텐츠를 추출하고, 필요한 경우 대화 기록을 준비하는 역할
    private Mono<Object> callFastApiBasedOnStep(int step, String sessionId, List<Chat> chatHistory, Chat userChat) {
        // 여기서만 updatedChatHistory를 생성합니다.
        // 2단계와 3단계 호출에 필요한 완전한 대화 기록을 준비합니다.
        List<Chat> updatedChatHistory = new ArrayList<>(chatHistory);
        updatedChatHistory.add(userChat);

        switch (step) {
            case 1:
                // 1단계는 최신 메시지(userChat.getContent())만 필요하므로, updatedChatHistory를 사용하지 않습니다.
                return aiService.callAnalyze1(sessionId, userChat.getContent())
                        .map(response -> (Object) response);
            case 2:
                // 2단계에서는 전체 대화 기록을 전달합니다.
                return aiService.callAnalyze2(sessionId, updatedChatHistory)
                        .map(response -> (Object) response);
            case 3:
                // 3단계에서도 전체 대화 기록을 전달합니다.
                return aiService.callAnalyze3(sessionId, updatedChatHistory)
                        .map(response -> (Object) response);
            default:
                return Mono.error(new IllegalArgumentException("잘못된 대화 단계입니다: " + step));
        }
    }

    // 4. AI 메시지를 저장하고 최종 응답 DTO를 만드는 역할
    private Mono<ChatResponseDTO> saveAiChatAndBuildResponse(Chat userChat, Object aiResponseDto) {
        String aiContent = "";
        String question = null;
        List<String> choices = null;
        String userPatternSummary = null;
        List<String> thoughtExamples = null;

        if (aiResponseDto instanceof ChatResponseDTO_1) {
            ChatResponseDTO_1 res1 = (ChatResponseDTO_1) aiResponseDto;
            aiContent = res1.getQuestion();
            question = res1.getQuestion();
            choices = res1.getChoices();
        } else if (aiResponseDto instanceof ChatResponseDTO_2) {
            ChatResponseDTO_2 res2 = (ChatResponseDTO_2) aiResponseDto;
            aiContent = res2.getResponse();
        } else if (aiResponseDto instanceof ChatResponseDTO_3) {
            ChatResponseDTO_3 res3 = (ChatResponseDTO_3) aiResponseDto;
            aiContent = res3.getGratitudeMessage();
            userPatternSummary = res3.getUserPatternSummary();
            question = res3.getQuestion();
            thoughtExamples = res3.getThoughtExamples();
        }

        Chat aiChat = Chat.builder()
                .sender(SenderType.AI)
                .content(aiContent)
                .sessionId(userChat.getSessionId())
                .step(userChat.getStep())
                .question(question)
                .choices(choices)
                .userPatternSummary(userPatternSummary)
                .thoughtExamples(thoughtExamples)
                .build();

        return chatRepository.save(aiChat)
                .map(savedAiChat -> ChatResponseDTO.from(savedAiChat));
    }

    private int getLastMessageStep(List<Chat> chatHistory) {
        if (chatHistory == null || chatHistory.isEmpty()) {
            return 0;
        }
        return chatHistory.stream()
                .filter(chat -> SenderType.AI.equals(chat.getSender()))
                .mapToInt(Chat::getStep)
                .max()
                .orElse(0);
    }

    // 채팅방을 생성
    public Mono<ChatRoomResponseDTO> createChatRoom(RoomType roomType) {
        ChatRoom chatRoom = ChatRoom.builder()
                .sessionId(UUID.randomUUID().toString())
                .roomType(roomType)
                .build();
        return chatRoomRepository.save(chatRoom)
                .map(savedRoom -> ChatRoomResponseDTO.from(savedRoom));
    }

    // 모든 채팅방 목록 조회
    public Flux<ChatRoomResponseDTO> getChatRooms() {
        return chatRoomRepository.findAll()
                .map(chatRoom -> ChatRoomResponseDTO.from(chatRoom));
    }

    // 특정 채팅방 메시지 목록 조회
    public Flux<ChatResponseDTO> getChatMessages(String sessionId) {
        return chatRepository.findBySessionId(sessionId)
                .map(chat -> ChatResponseDTO.from(chat));
    }
}
