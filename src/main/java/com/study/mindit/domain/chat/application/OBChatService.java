package com.study.mindit.domain.chat.application;

import com.study.mindit.domain.chat.domain.OBChatRoom;
import com.study.mindit.domain.chat.domain.OBConversation;
import com.study.mindit.domain.chat.domain.RoomType;
import com.study.mindit.domain.chat.domain.repository.OBChatRoomRepository;
import com.study.mindit.domain.chat.dto.obsession.request.OBChatRequestDTO_1;
import com.study.mindit.domain.chat.dto.obsession.request.OBConversationDTO;
import com.study.mindit.domain.chat.dto.obsession.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@Service
public class OBChatService {
    private final OBChatRoomRepository OBChatRoomRepository;
    private final OBAiService aiService;

    // 1. 전체 로직의 시작점이자 흐름을 조정하는 역할
    public Mono<OBChatResponseDTO> processChatMessage(OBChatRequestDTO_1 chatRequestDto) {
        log.info("=== ChatService.processChatMessage 시작 ===");
        log.info("입력: {}", chatRequestDto);
        
        return OBChatRoomRepository.findById(chatRequestDto.getSessionId())
                .flatMap(chatRoom -> {
                    log.info("=== ChatRoom 조회 완료. 현재 Step: {} ===", chatRoom.getCurrentStep());
                    
                    // 사용자 메시지를 conversation_history에 추가
                    chatRoom.addConversation("user", chatRequestDto.getContent());
                    chatRoom.incrementStep();
                    int nextStep = chatRoom.getCurrentStep();
                    
                    return OBChatRoomRepository.save(chatRoom)
                            .flatMap(savedRoom -> callFastApiAndUpdateRoom(savedRoom, nextStep));
                })
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom을 찾을 수 없습니다: " + chatRequestDto.getSessionId())));
    }

    // 2. FastAPI 호출 및 ChatRoom 업데이트
    private Mono<OBChatResponseDTO> callFastApiAndUpdateRoom(OBChatRoom chatRoom, int step) {
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
    private Mono<OBChatResponseDTO> updateRoomWithAiResponse(OBChatRoom chatRoom, Object aiResponseDto, int step) {
        // Step 8: 상황 선택을 임시 저장
        if (step == 8) {
            extractAndSaveSelectedSituations(chatRoom);
        }
        
        // Step 9: 점수 배열을 상황과 매핑
        if (step == 9) {
            mapScoresToSituations(chatRoom);
        }
        
        if (aiResponseDto instanceof OBChatResponseDTO_1) {
            OBChatResponseDTO_1 res1 = (OBChatResponseDTO_1) aiResponseDto;
            chatRoom.addConversation("assistant", aiResponseDto);
            
            return OBChatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep1(savedRoom, res1.getQuestion(), res1.getChoices()));
                    
        } else if (aiResponseDto instanceof OBChatResponseDTO_2) {
            OBChatResponseDTO_2 res2 = (OBChatResponseDTO_2) aiResponseDto;
            chatRoom.addConversation("assistant", aiResponseDto);
            
            return OBChatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep2(savedRoom, res2.getResponse()));
                    
        } else if (aiResponseDto instanceof OBChatResponseDTO_3) {
            OBChatResponseDTO_3 res3 = (OBChatResponseDTO_3) aiResponseDto;
            chatRoom.addConversation("assistant", aiResponseDto);
            
            return OBChatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep3(savedRoom, res3.getQuestion(), 
                            res3.getUserPatternSummary(), res3.getThoughtExamples(), res3.getGratitudeMessage()));
                            
        } else if (aiResponseDto instanceof OBChatResponseDTO_4) {
            OBChatResponseDTO_4 res4 = (OBChatResponseDTO_4) aiResponseDto;
            chatRoom.addConversation("assistant", aiResponseDto);
            
            return OBChatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep4(savedRoom, res4.getUserPatternSummary(), 
                            res4.getCategoryMessage(), res4.getEncouragement()));
                            
        } else if (aiResponseDto instanceof OBChatResponseDTO_5) {
            OBChatResponseDTO_5 res7 = (OBChatResponseDTO_5) aiResponseDto;
            chatRoom.addConversation("assistant", aiResponseDto);
            
            return OBChatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep7(savedRoom, res7.getIntroMessage(), 
                            res7.getQuestion(), res7.getSituations()));
                            
        } else if (aiResponseDto instanceof OBChatResponseDTO_6) {
            OBChatResponseDTO_6 res9 = (OBChatResponseDTO_6) aiResponseDto;
            chatRoom.addConversation("assistant", aiResponseDto);
            
            return OBChatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep9(savedRoom, res9.getIntroMessage(), 
                            res9.getAnxietyHierarchy(), res9.getPracticeMessage(), 
                            res9.getExampleMessage(), res9.getSupportMessage()));
        }
        
        return Mono.error(new RuntimeException("Unknown response type"));
    }

    // 4. ChatResponseDTO 생성 - Step 1
    private OBChatResponseDTO buildChatResponseStep1(OBChatRoom chatRoom, String question, List<String> choices) {
        return OBChatResponseDTO.builder()
                .question(question)
                .choices(choices)
                .sessionId(chatRoom.getId())
                .build();
    }
    
    // 4-2. ChatResponseDTO 생성 - Step 2
    private OBChatResponseDTO buildChatResponseStep2(OBChatRoom chatRoom, String aiContent) {
        return OBChatResponseDTO.builder()
                .response(aiContent)
                .sessionId(chatRoom.getId())
                .build();
    }
    
    // 4-3. ChatResponseDTO 생성 - Step 3
    private OBChatResponseDTO buildChatResponseStep3(OBChatRoom chatRoom, String question,
                                                     String userPatternSummary, List<String> thoughtExamples, String aiContent) {
        return OBChatResponseDTO.builder()
                .gratitudeMessage(aiContent)
                .userPatternSummary(userPatternSummary)
                .question(question)
                .thoughtExamples(thoughtExamples)
                .sessionId(chatRoom.getId())
                .build();
    }
    
    // 4-4. ChatResponseDTO 생성 - Step 4
    private OBChatResponseDTO buildChatResponseStep4(OBChatRoom chatRoom, String userPatternSummary,
                                                     String categoryMessage, String encouragement) {
        return OBChatResponseDTO.builder()
                .userPatternSummary(userPatternSummary)
                .categoryMessage(categoryMessage)
                .encouragement(encouragement)
                .sessionId(chatRoom.getId())
                .build();
    }
    
    // 4-5. ChatResponseDTO 생성 - Step 7
    private OBChatResponseDTO buildChatResponseStep7(OBChatRoom chatRoom, String introMessage,
                                                     String question, List<String> situations) {
        return OBChatResponseDTO.builder()
                .introMessage(introMessage)
                .question(question)
                .situations(situations)
                .sessionId(chatRoom.getId())
                .build();
    }
    
    // 4-6. ChatResponseDTO 생성 - Step 9
    private OBChatResponseDTO buildChatResponseStep9(OBChatRoom chatRoom, String introMessage,
                                                     List<OBAnxietyHierarchyDTO> anxietyHierarchy,
                                                     String practiceMessage, String exampleMessage, String supportMessage) {
        return OBChatResponseDTO.builder()
                .introMessage(introMessage)
                .anxietyHierarchy(anxietyHierarchy)
                .practiceMessage(practiceMessage)
                .exampleMessage(exampleMessage)
                .supportMessage(supportMessage)
                .sessionId(chatRoom.getId())
                .build();
    }

    // Step 8: conversation_history에서 선택된 상황 추출하여 저장
    private void extractAndSaveSelectedSituations(OBChatRoom chatRoom) {
        if (chatRoom.getConversationHistory() != null && !chatRoom.getConversationHistory().isEmpty()) {
            // 마지막 사용자 메시지 가져오기 (Step 8의 상황 선택)
            OBConversation lastUserMessage =
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
    private void mapScoresToSituations(OBChatRoom chatRoom) {
        if (chatRoom.getTempSelectedSituations() == null || chatRoom.getTempSelectedSituations().isEmpty()) {
            return;
        }

        // 마지막 사용자 메시지 가져오기 (Step 9의 점수 입력)
        OBConversation lastUserMessage =
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
    private List<OBConversationDTO> convertToRequestConversationItems(OBChatRoom chatRoom) {
        List<OBConversationDTO> items = new ArrayList<>();
        for (OBConversation item : chatRoom.getConversationHistory()) {
            items.add(new OBConversationDTO(
                    item.getRole(), 
                    item.getContent()
            ));
        }
        return items;
    }

    // 채팅방을 생성
    public Mono<OBChatRoomResponseDTO> createChatRoom(RoomType roomType) {
        OBChatRoom chatRoom = OBChatRoom.builder()
                .roomType(roomType)
                .build();
        return OBChatRoomRepository.save(chatRoom)
                .map(savedRoom -> OBChatRoomResponseDTO.from(savedRoom));
    }

    // 모든 채팅방 목록 조회
    public Flux<OBChatRoomResponseDTO> getChatRooms() {
        return OBChatRoomRepository.findAll()
                .map(chatRoom -> OBChatRoomResponseDTO.from(chatRoom));
    }

    // 특정 채팅방 메시지 목록 조회 - conversation_history 반환
    public Mono<OBChatRoom> getChatMessages(String sessionId) {
        return OBChatRoomRepository.findById(sessionId);
    }
    
    // 채팅방 Step 설정 (테스트용)
    public Mono<OBChatRoom> setStep(String sessionId, int step) {
        return OBChatRoomRepository.findById(sessionId)
                .flatMap(chatRoom -> {
                    chatRoom.setCurrentStep(step);
                    return OBChatRoomRepository.save(chatRoom);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom을 찾을 수 없습니다: " + sessionId)));
    }
}
