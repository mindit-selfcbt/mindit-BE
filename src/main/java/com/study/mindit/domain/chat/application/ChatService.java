package com.study.mindit.domain.chat.application;

import com.study.mindit.domain.chat.domain.ChatRoom;
import com.study.mindit.domain.chat.domain.RoomType;
import com.study.mindit.domain.chat.domain.repository.ChatRoomRepository;
import com.study.mindit.domain.chat.dto.request.ChatRequestDTO_1;
import com.study.mindit.domain.chat.dto.request.ConversationItemDTO;
import com.study.mindit.domain.chat.dto.response.*;
import com.study.mindit.domain.chat.dto.response.AnxietyHierarchyItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final AiService aiService;

    // 1. 전체 로직의 시작점이자 흐름을 조정하는 역할
    public Mono<ChatResponseDTO> processChatMessage(ChatRequestDTO_1 chatRequestDto) {
        log.info("=== ChatService.processChatMessage 시작 ===");
        log.info("입력: {}", chatRequestDto);
        
        return chatRoomRepository.findById(chatRequestDto.getSessionId())
                .flatMap(chatRoom -> {
                    log.info("=== ChatRoom 조회 완료. 현재 Step: {} ===", chatRoom.getCurrentStep());
                    
                    // 사용자 메시지를 conversation_history에 추가
                    chatRoom.addConversation("user", chatRequestDto.getContent());
                    chatRoom.incrementStep();
                    int nextStep = chatRoom.getCurrentStep();
                    
                    return chatRoomRepository.save(chatRoom)
                            .flatMap(savedRoom -> callFastApiAndUpdateRoom(savedRoom, nextStep));
                })
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom을 찾을 수 없습니다: " + chatRequestDto.getSessionId())));
    }

    // 2. FastAPI 호출 및 ChatRoom 업데이트
    private Mono<ChatResponseDTO> callFastApiAndUpdateRoom(ChatRoom chatRoom, int step) {
        String sessionId = chatRoom.getId();
        
        // Step별로 AI 호출
        Mono<Object> aiResponseMono;
        switch (step) {
            case 1:
                Object userContent = chatRoom.getConversationHistory().get(chatRoom.getConversationHistory().size() - 1).getContent();
                aiResponseMono = aiService.callAnalyze1(sessionId, userContent).map(response -> (Object) response);
                break;
            case 2:
                aiResponseMono = aiService.callAnalyze2(sessionId, convertToRequestConversationItems(chatRoom)).map(response -> (Object) response);
                break;
            case 3:
                aiResponseMono = aiService.callAnalyze3(sessionId, convertToRequestConversationItems(chatRoom)).map(response -> (Object) response);
                break;
            case 4:
                aiResponseMono = aiService.callAnalyze4(sessionId, convertToRequestConversationItems(chatRoom)).map(response -> (Object) response);
                break;
            case 5:
                aiResponseMono = aiService.callAnalyze5(sessionId, convertToRequestConversationItems(chatRoom)).map(response -> (Object) response);
                break;
            case 6:
                aiResponseMono = aiService.callAnalyze6(sessionId, convertToRequestConversationItems(chatRoom)).map(response -> (Object) response);
                break;
            case 7:
                aiResponseMono = aiService.callAnalyze7(sessionId, convertToRequestConversationItems(chatRoom)).map(response -> (Object) response);
                break;
            case 8:
                aiResponseMono = aiService.callAnalyze8(sessionId, convertToRequestConversationItems(chatRoom)).map(response -> (Object) response);
                break;
            case 9:
                aiResponseMono = aiService.callAnalyze9(sessionId, convertToRequestConversationItems(chatRoom)).map(response -> (Object) response);
                break;
            default:
                return Mono.error(new IllegalArgumentException("잘못된 대화 단계입니다: " + step));
        }
        
        return aiResponseMono.flatMap(aiResponseDto -> updateRoomWithAiResponse(chatRoom, aiResponseDto, step));
    }

    // 3. AI 응답으로 ChatRoom 업데이트
    private Mono<ChatResponseDTO> updateRoomWithAiResponse(ChatRoom chatRoom, Object aiResponseDto, int step) {
        // Step 8: 상황 선택을 임시 저장
        if (step == 8) {
            extractAndSaveSelectedSituations(chatRoom);
        }
        
        // Step 9: 점수 배열을 상황과 매핑
        if (step == 9) {
            mapScoresToSituations(chatRoom);
        }
        
        if (aiResponseDto instanceof ChatResponseDTO_1) {
            ChatResponseDTO_1 res1 = (ChatResponseDTO_1) aiResponseDto;
            String aiContent = res1.getQuestion();
            chatRoom.addConversation("assistant", aiContent);
            
            return chatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep1(savedRoom, res1.getQuestion(), res1.getChoices()));
                    
        } else if (aiResponseDto instanceof ChatResponseDTO_2) {
            ChatResponseDTO_2 res2 = (ChatResponseDTO_2) aiResponseDto;
            String aiContent = res2.getResponse();
            chatRoom.addConversation("assistant", aiContent);
            
            return chatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep2(savedRoom, aiContent));
                    
        } else if (aiResponseDto instanceof ChatResponseDTO_3) {
            ChatResponseDTO_3 res3 = (ChatResponseDTO_3) aiResponseDto;
            String aiContent = res3.getGratitudeMessage();
            chatRoom.addConversation("assistant", aiContent);
            
            return chatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep3(savedRoom, res3.getQuestion(), 
                            res3.getUserPatternSummary(), res3.getThoughtExamples(), aiContent));
                            
        } else if (aiResponseDto instanceof ChatResponseDTO_4) {
            ChatResponseDTO_4 res4 = (ChatResponseDTO_4) aiResponseDto;
            // Step 4는 userPatternSummary + categoryMessage + encouragement를 모두 합쳐서 저장
            String aiContent = res4.getUserPatternSummary() + "\n\n" + 
                               res4.getCategoryMessage() + "\n\n" + 
                               res4.getEncouragement();
            chatRoom.addConversation("assistant", aiContent);
            
            return chatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep4(savedRoom, res4.getUserPatternSummary(), 
                            res4.getCategoryMessage(), res4.getEncouragement()));
                            
        } else if (aiResponseDto instanceof ChatResponseDTO_5) {
            ChatResponseDTO_5 res7 = (ChatResponseDTO_5) aiResponseDto;
            // Step 7은 introMessage + question을 합쳐서 저장
            String aiContent = res7.getIntroMessage() + "\n\n" + res7.getQuestion();
            chatRoom.addConversation("assistant", aiContent);
            
            return chatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep7(savedRoom, res7.getIntroMessage(), 
                            res7.getQuestion(), res7.getSituations()));
                            
        } else if (aiResponseDto instanceof ChatResponseDTO_6) {
            ChatResponseDTO_6 res9 = (ChatResponseDTO_6) aiResponseDto;
            // Step 9는 모든 메시지를 합쳐서 저장
            String aiContent = res9.getIntroMessage() + "\n\n" + 
                               res9.getPracticeMessage() + "\n\n" + 
                               res9.getExampleMessage() + "\n\n" + 
                               res9.getSupportMessage();
            chatRoom.addConversation("assistant", aiContent);
            
            return chatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep9(savedRoom, res9.getIntroMessage(), 
                            res9.getAnxietyHierarchy(), res9.getPracticeMessage(), 
                            res9.getExampleMessage(), res9.getSupportMessage()));
        }
        
        return Mono.error(new RuntimeException("Unknown response type"));
    }

    // 4. ChatResponseDTO 생성 - Step 1
    private ChatResponseDTO buildChatResponseStep1(ChatRoom chatRoom, String question, List<String> choices) {
        return ChatResponseDTO.builder()
                .question(question)
                .choices(choices)
                .sessionId(chatRoom.getId())
                .build();
    }
    
    // 4-2. ChatResponseDTO 생성 - Step 2
    private ChatResponseDTO buildChatResponseStep2(ChatRoom chatRoom, String aiContent) {
        return ChatResponseDTO.builder()
                .response(aiContent)
                .sessionId(chatRoom.getId())
                .build();
    }
    
    // 4-3. ChatResponseDTO 생성 - Step 3
    private ChatResponseDTO buildChatResponseStep3(ChatRoom chatRoom, String question, 
                                                    String userPatternSummary, List<String> thoughtExamples, String aiContent) {
        return ChatResponseDTO.builder()
                .gratitudeMessage(aiContent)
                .userPatternSummary(userPatternSummary)
                .question(question)
                .thoughtExamples(thoughtExamples)
                .sessionId(chatRoom.getId())
                .build();
    }
    
    // 4-4. ChatResponseDTO 생성 - Step 4
    private ChatResponseDTO buildChatResponseStep4(ChatRoom chatRoom, String userPatternSummary, 
                                                    String categoryMessage, String encouragement) {
        return ChatResponseDTO.builder()
                .userPatternSummary(userPatternSummary)
                .categoryMessage(categoryMessage)
                .encouragement(encouragement)
                .sessionId(chatRoom.getId())
                .build();
    }
    
    // 4-5. ChatResponseDTO 생성 - Step 7
    private ChatResponseDTO buildChatResponseStep7(ChatRoom chatRoom, String introMessage, 
                                                    String question, List<String> situations) {
        return ChatResponseDTO.builder()
                .introMessage(introMessage)
                .question(question)
                .situations(situations)
                .sessionId(chatRoom.getId())
                .build();
    }
    
    // 4-6. ChatResponseDTO 생성 - Step 9
    private ChatResponseDTO buildChatResponseStep9(ChatRoom chatRoom, String introMessage, 
                                                    List<AnxietyHierarchyItemDTO> anxietyHierarchy,
                                                    String practiceMessage, String exampleMessage, String supportMessage) {
        return ChatResponseDTO.builder()
                .introMessage(introMessage)
                .anxietyHierarchy(anxietyHierarchy)
                .practiceMessage(practiceMessage)
                .exampleMessage(exampleMessage)
                .supportMessage(supportMessage)
                .sessionId(chatRoom.getId())
                .build();
    }

    // Step 8: conversation_history에서 선택된 상황 추출하여 저장
    private void extractAndSaveSelectedSituations(ChatRoom chatRoom) {
        if (chatRoom.getConversationHistory() != null && !chatRoom.getConversationHistory().isEmpty()) {
            // 마지막 사용자 메시지 가져오기 (Step 8의 상황 선택)
            com.study.mindit.domain.chat.domain.ConversationItem lastUserMessage = 
                chatRoom.getConversationHistory().get(chatRoom.getConversationHistory().size() - 1);
            
            Object content = lastUserMessage.getContent();
            
            // content가 Map 형태인지 확인
            if (content instanceof java.util.Map) {
                java.util.Map<String, Object> contentMap = (java.util.Map<String, Object>) content;
                
                // selected_situations 추출
                if (contentMap.containsKey("selected_situations")) {
                    List<String> situations = (List<String>) contentMap.get("selected_situations");
                    chatRoom.setTempSelectedSituations(situations);
                }
            }
        }
    }
    
    // Step 9: 점수 배열을 받아서 상황과 매핑
    private void mapScoresToSituations(ChatRoom chatRoom) {
        if (chatRoom.getTempSelectedSituations() == null || chatRoom.getTempSelectedSituations().isEmpty()) {
            return;
        }
        
        // 마지막 사용자 메시지 가져오기 (Step 9의 점수 입력)
        com.study.mindit.domain.chat.domain.ConversationItem lastUserMessage = 
            chatRoom.getConversationHistory().get(chatRoom.getConversationHistory().size() - 1);
        
        Object content = lastUserMessage.getContent();
        
        // content가 List (점수 배열)인 경우
        if (content instanceof java.util.List) {
            List<Integer> scores = (List<Integer>) content;
            List<String> situations = chatRoom.getTempSelectedSituations();
            
            // 매핑된 객체 생성
            java.util.Map<String, Object> mappedData = new java.util.HashMap<>();
            mappedData.put("type", "anxiety_scores");
            
            java.util.List<java.util.Map<String, Object>> situationScores = new java.util.ArrayList<>();
            for (int i = 0; i < Math.min(situations.size(), scores.size()); i++) {
                java.util.Map<String, Object> item = new java.util.HashMap<>();
                item.put("situation", situations.get(i));
                item.put("score", scores.get(i));
                situationScores.add(item);
            }
            
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("situations", situationScores);
            mappedData.put("data", data);
            
            // conversation_history의 마지막 항목을 매핑된 객체로 업데이트
            chatRoom.getConversationHistory().remove(chatRoom.getConversationHistory().size() - 1);
            chatRoom.addConversation("user", mappedData);
        }
    }
    
    // ChatRoom의 conversation_history를 Request DTO용 ConversationItem으로 변환
    private List<ConversationItemDTO> convertToRequestConversationItems(ChatRoom chatRoom) {
        List<ConversationItemDTO> items = new ArrayList<>();
        for (com.study.mindit.domain.chat.domain.ConversationItem item : chatRoom.getConversationHistory()) {
            items.add(new ConversationItemDTO(
                    item.getRole(), 
                    item.getContent()
            ));
        }
        return items;
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

    // 특정 채팅방 메시지 목록 조회 - conversation_history 반환
    public Mono<ChatRoom> getChatMessages(String sessionId) {
        return chatRoomRepository.findById(sessionId);
    }
}
