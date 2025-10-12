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
    public Flux<OBChatResponseDTO> processChatMessage(OBChatRequestDTO_1 chatRequestDto) {
        log.info("=== ChatService.processChatMessage 시작 ===");
        log.info("입력: {}", chatRequestDto);
        
        return OBChatRoomRepository.findById(chatRequestDto.getSessionId())
                .flatMapMany(chatRoom -> {
                    log.info("=== ChatRoom 조회 완료. 현재 Step: {} ===", chatRoom.getCurrentStep());
                    
                    // 사용자 메시지를 conversation_history에 추가
                    chatRoom.addConversation("user", chatRequestDto.getContent());
                    chatRoom.incrementStep();
                    int nextStep = chatRoom.getCurrentStep();
                    
                    return OBChatRoomRepository.save(chatRoom)
                            .flatMapMany(savedRoom -> callFastApiAndUpdateRoom(savedRoom, nextStep));
                })
                .switchIfEmpty(Flux.error(new RuntimeException("ChatRoom을 찾을 수 없습니다: " + chatRequestDto.getSessionId())));
    }

    // 2-1. Step 4와 5를 연속으로 호출 (Flux로 두 응답을 순차적으로 반환)
    private Flux<OBChatResponseDTO> callAnalyze4And5Together(OBChatRoom chatRoom, String sessionId) {
        return aiService.callAnalyze4(sessionId, convertToRequestConversationItems(chatRoom))
                .flatMap(res4 -> {
                    // Step 4 응답을 conversation_history에 추가
                    chatRoom.addConversation("assistant", res4);
                    chatRoom.incrementStep(); // Step 5로 증가
                    
                    // ChatRoom 저장 후 Step 4 응답 반환
                    return OBChatRoomRepository.save(chatRoom)
                            .map(savedRoom -> buildChatResponseStep4(
                                    savedRoom,
                                    res4.getUserPatternSummary(),
                                    res4.getCategoryMessage(),
                                    res4.getEncouragement()
                            ));
                })
                .flux()
                .concatWith(
                    // Step 4가 완료된 후에 Step 5 호출
                    aiService.callAnalyze5(sessionId, convertToRequestConversationItems(chatRoom))
                            .flatMap(res5 -> {
                                // Step 5 응답을 conversation_history에 추가
                                chatRoom.addConversation("assistant", res5);
                                // incrementStep() 하지 않음! 다음 사용자 메시지에서 processChatMessage가 Step 6으로 증가시킴
                                
                                // ChatRoom 저장 후 Step 5 응답 반환
                                return OBChatRoomRepository.save(chatRoom)
                                        .map(savedRoom -> buildChatResponseStep2(savedRoom, res5.getResponse()));
                            })
                );
    }
    
    // 2-2. Step 6과 7을 연속으로 호출 (Flux로 두 응답을 순차적으로 반환)
    private Flux<OBChatResponseDTO> callAnalyze6And7Together(OBChatRoom chatRoom, String sessionId) {
        return aiService.callAnalyze6(sessionId, convertToRequestConversationItems(chatRoom))
                .flatMap(res6 -> {
                    // Step 6 응답을 conversation_history에 추가
                    chatRoom.addConversation("assistant", res6);
                    chatRoom.incrementStep(); // Step 7로 증가
                    
                    // ChatRoom 저장 후 Step 6 응답 반환
                    return OBChatRoomRepository.save(chatRoom)
                            .map(savedRoom -> buildChatResponseStep2(savedRoom, res6.getResponse()));
                })
                .flux()
                .concatWith(
                    // Step 6이 완료된 후에 Step 7 호출
                    aiService.callAnalyze7(sessionId, convertToRequestConversationItems(chatRoom))
                            .flatMap(res7 -> {
                                // Step 7 응답을 conversation_history에 추가
                                chatRoom.addConversation("assistant", res7);
                                // incrementStep() 하지 않음! 다음 사용자 메시지에서 processChatMessage가 Step 8로 증가시킴
                                
                                // ChatRoom 저장 후 Step 7 응답 반환
                                return OBChatRoomRepository.save(chatRoom)
                                        .map(savedRoom -> buildChatResponseStep7(
                                                savedRoom,
                                                res7.getIntroMessage(),
                                                res7.getQuestion(),
                                                res7.getSituations()
                                        ));
                            })
                );
    }

    // 2. FastAPI 호출 및 ChatRoom 업데이트
    private Flux<OBChatResponseDTO> callFastApiAndUpdateRoom(OBChatRoom chatRoom, int step) {
        String sessionId = chatRoom.getId();
        
        // Step 4는 특별 처리 (Step 4와 5를 연속으로 호출)
        if (step == 4) {
            return callAnalyze4And5Together(chatRoom, sessionId);
        }
        
        // Step 6도 특별 처리 (Step 6과 7을 연속으로 호출)
        if (step == 6) {
            return callAnalyze6And7Together(chatRoom, sessionId);
        }
        
        // 나머지 Step들은 단일 응답
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
            case 5:
                aiResponseMono = aiService.callAnalyze5(sessionId, convertToRequestConversationItems(chatRoom)).map(response -> (Object) response);
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
                return Flux.error(new IllegalArgumentException("잘못된 대화 단계입니다: " + step));
        }
        
        return aiResponseMono
                .flatMap(aiResponseDto -> updateRoomWithAiResponse(chatRoom, aiResponseDto, step))
                .flux();
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
            log.info("=== Step 9: mapScoresToSituations 완료 ===");
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
                            
        } else if (aiResponseDto instanceof OBChatResponseDTO_7) {
            OBChatResponseDTO_7 res7 = (OBChatResponseDTO_7) aiResponseDto;
            chatRoom.addConversation("assistant", aiResponseDto);
            
            return OBChatRoomRepository.save(chatRoom)
                    .map(savedRoom -> buildChatResponseStep7(savedRoom, res7.getIntroMessage(), 
                            res7.getQuestion(), res7.getSituations()));
                            
        } else if (aiResponseDto instanceof OBChatResponseDTO_9) {
            OBChatResponseDTO_9 res9 = (OBChatResponseDTO_9) aiResponseDto;
            chatRoom.addConversation("assistant", aiResponseDto);
            
            // Step 9는 항상 chatRoom에서 anxiety_hierarchy를 생성
            return OBChatRoomRepository.save(chatRoom)
                    .map(savedRoom -> {
                        List<OBAnxietyHierarchyDTO> anxietyHierarchy = createAnxietyHierarchyFromChatRoom(savedRoom);
                        log.info("=== Step 9: anxiety_hierarchy 생성 완료. 크기: {} ===", anxietyHierarchy.size());
                        return buildChatResponseStep9(savedRoom, res9.getIntroMessage(), 
                                anxietyHierarchy, res9.getPracticeMessage(), 
                                res9.getExampleMessage(), res9.getSupportMessage());
                    });
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
    
    // ChatRoom에서 anxiety_hierarchy 생성
    private List<OBAnxietyHierarchyDTO> createAnxietyHierarchyFromChatRoom(OBChatRoom chatRoom) {
        List<OBAnxietyHierarchyDTO> anxietyHierarchy = new ArrayList<>();
        
        log.info("=== createAnxietyHierarchyFromChatRoom 시작 ===");
        
        // conversation_history에서 anxiety_scores 데이터 찾기
        if (chatRoom.getConversationHistory() != null) {
            log.info("conversation_history 크기: {}", chatRoom.getConversationHistory().size());
            
            for (int idx = 0; idx < chatRoom.getConversationHistory().size(); idx++) {
                OBConversation conversation = chatRoom.getConversationHistory().get(idx);
                Object content = conversation.getContent();
                
                log.info("conversation[{}] - role: {}, content type: {}", idx, conversation.getRole(), 
                         content != null ? content.getClass().getSimpleName() : "null");
                
                if (content instanceof java.util.Map) {
                    java.util.Map<String, Object> contentMap = (java.util.Map<String, Object>) content;
                    Object typeValue = contentMap.get("type");
                    
                    log.info("conversation[{}] - Map의 type: {}", idx, typeValue);
                    
                    if ("anxiety_scores".equals(typeValue)) {
                        log.info("=== anxiety_scores 찾음! ===");
                        
                        java.util.Map<String, Object> data = (java.util.Map<String, Object>) contentMap.get("data");
                        if (data != null) {
                            java.util.List<java.util.Map<String, Object>> situations = 
                                (java.util.List<java.util.Map<String, Object>>) data.get("situations");
                            
                            if (situations != null && !situations.isEmpty()) {
                                log.info("상황 개수: {}", situations.size());
                                
                                // 점수순으로 정렬 (낮은 점수 -> 높은 점수)
                                situations.sort((a, b) -> {
                                    Integer scoreA = (Integer) a.get("score");
                                    Integer scoreB = (Integer) b.get("score");
                                    return scoreA.compareTo(scoreB);
                                });
                                
                                // OBAnxietyHierarchyDTO로 변환
                                for (int i = 0; i < situations.size(); i++) {
                                    java.util.Map<String, Object> situation = situations.get(i);
                                    String situationText = (String) situation.get("situation");
                                    Integer score = (Integer) situation.get("score");
                                    
                                    log.info("상황[{}] - situation: {}, score: {}", i + 1, situationText, score);
                                    
                                    anxietyHierarchy.add(OBAnxietyHierarchyDTO.builder()
                                            .order(i + 1)
                                            .situation(situationText)
                                            .score(score)
                                            .build());
                                }
                                break;
                            } else {
                                log.warn("situations가 null이거나 비어있음");
                            }
                        } else {
                            log.warn("data가 null임");
                        }
                    }
                }
            }
        } else {
            log.warn("conversation_history가 null임");
        }
        
        log.info("=== createAnxietyHierarchyFromChatRoom 완료. 생성된 항목 수: {} ===", anxietyHierarchy.size());
        
        return anxietyHierarchy;
    }

    // Step 8: conversation_history에서 선택된 상황 추출하여 저장
    // 프론트엔드 형식: {selected_situations: ["상황1", "상황2", ...]}
    private void extractAndSaveSelectedSituations(OBChatRoom chatRoom) {
        if (chatRoom.getConversationHistory() == null || chatRoom.getConversationHistory().isEmpty()) {
            return;
        }
        
        OBConversation lastUserMessage =
            chatRoom.getConversationHistory().get(chatRoom.getConversationHistory().size() - 1);
        
        Object content = lastUserMessage.getContent();

        if (!(content instanceof java.util.Map)) {
            throw new IllegalArgumentException("Step 8은 반드시 {selected_situations: []} 형식으로 전송해야 합니다.");
        }
        
        java.util.Map<String, Object> contentMap = (java.util.Map<String, Object>) content;
        
        if (!contentMap.containsKey("selected_situations")) {
            throw new IllegalArgumentException("Step 8은 반드시 selected_situations 키를 포함해야 합니다.");
        }
        
        List<String> situations = (List<String>) contentMap.get("selected_situations");
        chatRoom.setTempSelectedSituations(situations);
        
        log.info("Step 8: 저장된 상황 수: {}", situations.size());
    }
    
    // Step 9: 점수 배열을 받아서 상황과 매핑
    // 프론트엔드 형식: [95, 80, 90, 70]
    private void mapScoresToSituations(OBChatRoom chatRoom) {
        if (chatRoom.getTempSelectedSituations() == null || chatRoom.getTempSelectedSituations().isEmpty()) {
            throw new IllegalStateException("Step 8에서 저장된 상황이 없습니다.");
        }

        OBConversation lastUserMessage =
            chatRoom.getConversationHistory().get(chatRoom.getConversationHistory().size() - 1);
        
        Object content = lastUserMessage.getContent();
        
        if (!(content instanceof java.util.List)) {
            throw new IllegalArgumentException("Step 9는 반드시 List<Integer> 형식으로 전송해야 합니다. 예: [95, 80, 90, 70]");
        }
        
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
        
        log.info("Step 9: {}개 상황-점수 매핑 완료", situationScores.size());
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
                    
                    // conversation_history를 해당 step까지만 유지
                    List<OBConversation> conversationHistory = chatRoom.getConversationHistory();
                    if (conversationHistory != null && !conversationHistory.isEmpty()) {
                        // step * 2까지만 유지 (사용자 메시지 + AI 응답 = 2개씩)
                        int maxItems = step * 2;
                        if (conversationHistory.size() > maxItems) {
                            List<OBConversation> trimmedHistory = new ArrayList<>(
                                conversationHistory.subList(0, maxItems)
                            );
                            chatRoom.setConversationHistory(trimmedHistory);
                        }
                    }
                    
                    return OBChatRoomRepository.save(chatRoom);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom을 찾을 수 없습니다: " + sessionId)));
    }
}
