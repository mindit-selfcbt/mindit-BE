package com.study.mindit.domain.chat.application;

import com.study.mindit.domain.chat.domain.CognitionChatRoom;
import com.study.mindit.domain.chat.domain.CognitionConversation;
import com.study.mindit.domain.chat.dto.cognition.request.CognitionChatRequestDTO_1;
import com.study.mindit.domain.chat.dto.cognition.request.CognitionConversationDTO;
import com.study.mindit.domain.chat.dto.cognition.response.CognitionChatResponseDTO;
import com.study.mindit.domain.chat.domain.repository.CognitionChatRoomRepository;
import com.study.mindit.domain.prevention.domain.repository.PreventionReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CognitionChatService {
    
    private final CognitionAiService aiService;
    private final PreventionReportRepository preventionReportRepository;
    private final CognitionChatRoomRepository cognitionChatRoomRepository;

    // 인지적 오류 파악하기 메시지 처리
    public Mono<CognitionChatResponseDTO> processCognitionMessage(CognitionChatRequestDTO_1 request) {
        log.info("=== CognitionChatService.processCognitionMessage 시작 ===");
        log.info("입력 sessionId: {}", request.getSessionId());
        
        // CognitionChatRoom 조회 또는 생성
        return cognitionChatRoomRepository.findById(request.getSessionId())
                .switchIfEmpty(createNewCognitionChatRoom(request.getSessionId()))
                .flatMap(cognitionChatRoom -> {
                    log.info("=== CognitionChatRoom 조회/생성 완료 ===");
                    log.info("현재 Step: {}", cognitionChatRoom.getCurrentStep());
                    
                    // Step 1인 경우: prevention_reports에서 데이터 조회하여 첫 번째 응답 생성
                    if (cognitionChatRoom.getCurrentStep() == 1) {
                        return generateInitialResponse(request.getSessionId(), cognitionChatRoom);
                    } else {
                        // Step 2 이상: 사용자 메시지를 conversation_history에 추가하고 AI 응답 생성
                        return processUserMessage(request, cognitionChatRoom);
                    }
                })
                .doOnNext(response -> log.info("=== 최종 응답 생성 완료: {} ===", response))
                .doOnError(error -> log.error("=== Cognition 처리 에러 ===", error));
    }

    // 새로운 CognitionChatRoom 생성
    private Mono<CognitionChatRoom> createNewCognitionChatRoom(String sessionId) {
        CognitionChatRoom newRoom = CognitionChatRoom.builder()
                .roomType(com.study.mindit.domain.chat.domain.RoomType.COGNITION)
                .currentStep(1)
                .build();
        return cognitionChatRoomRepository.save(newRoom);
    }

    // 초기 응답 생성 (Step 1)
    private Mono<CognitionChatResponseDTO> generateInitialResponse(String sessionId, CognitionChatRoom cognitionChatRoom) {
        return preventionReportRepository.findBySessionIdOrderByCreatedAtDesc(sessionId)
                .filter(report -> report.getIsCompleted() != null && report.getIsCompleted())
                .switchIfEmpty(Mono.error(new RuntimeException("완료된 반응방지 리포트를 찾을 수 없습니다. sessionId: " + sessionId)))
                .next()
                .flatMap(preventionReport -> {
                    log.info("=== PreventionReport 조회 완료 ===");
                    
                    // 사용자 메시지를 conversation_history에 추가
                    java.util.Map<String, Object> userContent = java.util.Map.of(
                        "obsession_situation", preventionReport.getObsessionSituation(),
                        "anxiety_level_start", preventionReport.getAnxietyLevelStart(),
                        "anxiety_level_end", preventionReport.getAnxietyLevelEnd(),
                        "obsession_thought", preventionReport.getObsessionThought()
                    );
                    
                    cognitionChatRoom.addConversation("user", userContent);
                    
                    // AI 서비스 호출 (process_chat_1)
                    return aiService.callProcessChat1(
                            sessionId,
                            preventionReport.getObsessionSituation(),
                            preventionReport.getAnxietyLevelStart(),
                            preventionReport.getAnxietyLevelEnd(),
                            preventionReport.getObsessionThought()
                    )
                            .flatMap(aiResponse -> {
                                // AI 응답을 conversation_history에 추가
                                java.util.Map<String, Object> assistantContent = java.util.Map.of(
                                    "encouragement_message", aiResponse.getEncouragementMessage(),
                                    "cognitive_error_explanation", aiResponse.getCognitiveErrorExplanation(),
                                    "personalized_situation", aiResponse.getPersonalizedSituation(),
                                    "emotion_question", aiResponse.getEmotionQuestion()
                                );
                                
                                cognitionChatRoom.addConversation("assistant", assistantContent);
                                cognitionChatRoom.incrementStep();
                                
                                // 채팅방 저장
                                return cognitionChatRoomRepository.save(cognitionChatRoom)
                                        .map(savedRoom -> CognitionChatResponseDTO.builder()
                                                .sessionId(sessionId)
                                                .obsessionType(aiResponse.getObsessionType())
                                                .encouragementMessage(aiResponse.getEncouragementMessage())
                                                .cognitiveErrorExplanation(aiResponse.getCognitiveErrorExplanation())
                                                .personalizedSituation(aiResponse.getPersonalizedSituation())
                                                .emotionQuestion(aiResponse.getEmotionQuestion())
                                                .build());
                            });
                });
    }

    // 확인강박 Step 5: 사용자 선택에 따른 분기
    private Mono<CognitionChatResponseDTO> processCheckObsessionStep5(CognitionChatRequestDTO_1 request, CognitionChatRoom cognitionChatRoom) {
        // TODO: 사용자 선택에 따라 process_chat_6 또는 process_chat_7로 분기
        // 현재는 임시로 process_chat_7 (마무리) 호출
        
        String obsessionType = extractObsessionTypeFromHistory(cognitionChatRoom);
        List<CognitionConversationDTO> conversationHistory = convertToConversationDTOs(cognitionChatRoom);
        
        // 마무리 선택
        return aiService.callProcessChat7(request.getSessionId(), obsessionType, conversationHistory)
                .flatMap(aiResponse -> {
                    cognitionChatRoom.addConversation("assistant", aiResponse.getCompletionMessage());
                    cognitionChatRoom.incrementStep();
                    
                    return cognitionChatRoomRepository.save(cognitionChatRoom)
                            .map(savedRoom -> CognitionChatResponseDTO.builder()
                                    .sessionId(request.getSessionId())
                                    .completionMessage(aiResponse.getCompletionMessage())
                                    .build());
                });
    }

    // 오염강박 Step 5: 사용자 선택에 따른 분기
    private Mono<CognitionChatResponseDTO> processContaminationObsessionStep5(CognitionChatRequestDTO_1 request, CognitionChatRoom cognitionChatRoom) {
        // TODO: 사용자 선택에 따라 process_chat_6 또는 process_chat_7로 분기
        // 현재는 임시로 process_chat_6 호출
        
        String obsessionType = extractObsessionTypeFromHistory(cognitionChatRoom);
        List<CognitionConversationDTO> conversationHistory = convertToConversationDTOs(cognitionChatRoom);
        
        // 마무리 선택
        return aiService.callProcessChat7(request.getSessionId(), obsessionType, conversationHistory)
                .flatMap(aiResponse -> {
                    cognitionChatRoom.addConversation("assistant", aiResponse.getCompletionMessage());
                    cognitionChatRoom.incrementStep();
                    
                    return cognitionChatRoomRepository.save(cognitionChatRoom)
                            .map(savedRoom -> CognitionChatResponseDTO.builder()
                                    .sessionId(request.getSessionId())
                                    .completionMessage(aiResponse.getCompletionMessage())
                                    .build());
                });
    }

    // 사용자 메시지 처리 (Step 2 이상)
    private Mono<CognitionChatResponseDTO> processUserMessage(CognitionChatRequestDTO_1 request, CognitionChatRoom cognitionChatRoom) {
        log.info("=== 사용자 메시지 처리 시작 - Step: {} ===", cognitionChatRoom.getCurrentStep());
        
        // Step 2: conversation_history 조회
        if (cognitionChatRoom.getCurrentStep() == 2) {
            return processStep2UserResponse(request, cognitionChatRoom);
        }
        // Step 3: obsession_type에 따른 분기 처리
        else if (cognitionChatRoom.getCurrentStep() == 3) {
            return processStep3UserResponse(request, cognitionChatRoom);
        }
        // Step 4: obsession_type에 따른 분기 처리
        else if (cognitionChatRoom.getCurrentStep() == 4) {
            return processStep4UserResponse(request, cognitionChatRoom);
        }
        // Step 5: 사용자 선택 처리
        else if (cognitionChatRoom.getCurrentStep() == 5) {
            return processStep5UserResponse(request, cognitionChatRoom);
        }
        
        return Mono.error(new RuntimeException("지원하지 않는 Step입니다: " + cognitionChatRoom.getCurrentStep()));
    }

    // Step 2: conversation_history 조회 (process_chat_2 연결)
    private Mono<CognitionChatResponseDTO> processStep2UserResponse(CognitionChatRequestDTO_1 request, CognitionChatRoom cognitionChatRoom) {
        // 사용자 입력이 있는 경우에만 conversation_history에 추가
        if (request.getUserText() != null && !request.getUserText().trim().isEmpty()) {
            cognitionChatRoom.addConversation("user", request.getUserText());
        }
        
        // obsession_type 확인 (첫 번째 AI 응답에서 가져옴)
        String obsessionType = extractObsessionTypeFromHistory(cognitionChatRoom);
        
        // conversation_history를 DTO로 변환
        List<CognitionConversationDTO> conversationHistory = convertToConversationDTOs(cognitionChatRoom);
        
        // process_chat_2 연결
        return aiService.callProcessChat2(
                request.getSessionId(),
                obsessionType,
                conversationHistory
        )
                .flatMap(aiResponse -> {
                    // AI 응답을 conversation_history에 추가
                    java.util.Map<String, Object> assistantContent = java.util.Map.of(
                        "encouragement_message", aiResponse.getEncouragementMessage(),
                        "cognitive_error_explanation", aiResponse.getCognitiveErrorExplanation(),
                        "personalized_situation", aiResponse.getPersonalizedSituation(),
                        "emotion_question", aiResponse.getEmotionQuestion()
                    );
                    
                    cognitionChatRoom.addConversation("assistant", assistantContent);
                    cognitionChatRoom.incrementStep();
                    
                    // 채팅방 저장
                    return cognitionChatRoomRepository.save(cognitionChatRoom)
                            .map(savedRoom -> CognitionChatResponseDTO.builder()
                                    .sessionId(request.getSessionId())
                                    .obsessionType(aiResponse.getObsessionType())
                                    .encouragementMessage(aiResponse.getEncouragementMessage())
                                    .cognitiveErrorExplanation(aiResponse.getCognitiveErrorExplanation())
                                    .personalizedSituation(aiResponse.getPersonalizedSituation())
                                    .emotionQuestion(aiResponse.getEmotionQuestion())
                                    .build());
                });
    }

    // Step 3: obsession_type에 따른 분기 처리
    private Mono<CognitionChatResponseDTO> processStep3UserResponse(CognitionChatRequestDTO_1 request, CognitionChatRoom cognitionChatRoom) {
        // 사용자 입력이 있는 경우에만 conversation_history에 추가
        if (request.getUserText() != null && !request.getUserText().trim().isEmpty()) {
            cognitionChatRoom.addConversation("user", request.getUserText());
        }
        
        // obsession_type 확인 (첫 번째 AI 응답에서 가져옴)
        String obsessionType = extractObsessionTypeFromHistory(cognitionChatRoom);
        
        // conversation_history를 DTO로 변환
        List<CognitionConversationDTO> conversationHistory = convertToConversationDTOs(cognitionChatRoom);
        
        // obsession_type에 따라 다른 AI 서비스 호출
        if ("확인강박".equals(obsessionType)) {
            // 확인강박: process_chat_3 호출
            return aiService.callProcessChat3(request.getSessionId(), obsessionType, conversationHistory)
                    .flatMap(aiResponse -> {
                        // AI 응답을 conversation_history에 추가
                        java.util.Map<String, Object> assistantContent = java.util.Map.of(
                            "empathy_message", aiResponse.getEmpathyMessage(),
                            "insight_message", aiResponse.getInsightMessage(),
                            "encouragement_message", aiResponse.getEncouragementMessage(),
                            "choice_question", aiResponse.getChoiceQuestion()
                        );
                        
                        cognitionChatRoom.addConversation("assistant", assistantContent);
                        cognitionChatRoom.incrementStep();
                        
                        // 채팅방 저장
                        return cognitionChatRoomRepository.save(cognitionChatRoom)
                                .map(savedRoom -> CognitionChatResponseDTO.builder()
                                        .sessionId(request.getSessionId())
                                        .empathyMessage(aiResponse.getEmpathyMessage())
                                        .insightMessage(aiResponse.getInsightMessage())
                                        .encouragementMessage(aiResponse.getEncouragementMessage())
                                        .choiceQuestion(aiResponse.getChoiceQuestion())
                                        .build());
                    });
        } else if ("오염강박".equals(obsessionType)) {
            // 오염강박: process_chat_4 호출
            return aiService.callProcessChat4(request.getSessionId(), obsessionType, conversationHistory)
                    .flatMap(aiResponse -> {
                        // AI 응답을 conversation_history에 추가
                        cognitionChatRoom.addConversation("assistant", aiResponse.getResponse());
                        cognitionChatRoom.incrementStep();
                        
                        // 채팅방 저장
                        return cognitionChatRoomRepository.save(cognitionChatRoom)
                                .map(savedRoom -> CognitionChatResponseDTO.builder()
                                        .sessionId(request.getSessionId())
                                        .response(aiResponse.getResponse())
                                        .build());
                    });
        } else {
            // 기타 강박유형: 기본 처리
            return Mono.error(new RuntimeException("지원하지 않는 강박유형입니다: " + obsessionType));
        }
    }

    // Step 4: obsession_type에 따른 분기 처리
    private Mono<CognitionChatResponseDTO> processStep4UserResponse(CognitionChatRequestDTO_1 request, CognitionChatRoom cognitionChatRoom) {
        // 사용자 입력이 있는 경우에만 conversation_history에 추가
        if (request.getUserText() != null && !request.getUserText().trim().isEmpty()) {
            cognitionChatRoom.addConversation("user", request.getUserText());
        }
        
        // obsession_type 확인 (첫 번째 AI 응답에서 가져옴)
        String obsessionType = extractObsessionTypeFromHistory(cognitionChatRoom);
        
        // obsession_type에 따라 다른 처리
        if ("확인강박".equals(obsessionType)) {
            // 확인강박: 사용자 선택에 따라 process_chat_6 또는 process_chat_7
            return processCheckObsessionStep4(request, cognitionChatRoom);
        } else if ("오염강박".equals(obsessionType)) {
            // 오염강박: process_chat_5 호출
            return processContaminationObsessionStep4(request, cognitionChatRoom);
        } else {
            return Mono.error(new RuntimeException("지원하지 않는 강박유형입니다: " + obsessionType));
        }
    }

    // Step 5: 사용자 선택 처리
    private Mono<CognitionChatResponseDTO> processStep5UserResponse(CognitionChatRequestDTO_1 request, CognitionChatRoom cognitionChatRoom) {
        
        // obsession_type 확인 (첫 번째 AI 응답에서 가져옴)
        String obsessionType = extractObsessionTypeFromHistory(cognitionChatRoom);
        
        // 사용자 입력이 있는 경우에만 conversation_history에 추가
        if (request.getUserText() != null && !request.getUserText().trim().isEmpty()) {
            cognitionChatRoom.addConversation("user", request.getUserText());
        }
        
        // obsession_type에 따라 다른 처리
        if ("확인강박".equals(obsessionType)) {
            // 확인강박: 사용자 선택에 따라 process_chat_6 또는 process_chat_7
            return processCheckObsessionStep5(request, cognitionChatRoom);
        } else if ("오염강박".equals(obsessionType)) {
            // 오염강박: 사용자 선택에 따라 process_chat_6 또는 process_chat_7
            return processContaminationObsessionStep5(request, cognitionChatRoom);
        } else {
            return Mono.error(new RuntimeException("지원하지 않는 강박유형입니다: " + obsessionType));
        }
    }

    // 확인강박 Step 4: 추가 대화 (process_chat_6 연결)
    private Mono<CognitionChatResponseDTO> processCheckObsessionStep4(CognitionChatRequestDTO_1 request, CognitionChatRoom cognitionChatRoom) {
        
        // obsession_type 확인 (첫 번째 AI 응답에서 가져옴)
        String obsessionType = extractObsessionTypeFromHistory(cognitionChatRoom);
        
        // conversation_history를 DTO로 변환
        List<CognitionConversationDTO> conversationHistory = convertToConversationDTOs(cognitionChatRoom);
        
        // 임시로 마무리 선택 (process_chat_7 연결)
        return aiService.callProcessChat7(request.getSessionId(), obsessionType, conversationHistory)
                .flatMap(aiResponse -> {
                    // AI 응답을 conversation_history에 추가
                    cognitionChatRoom.addConversation("assistant", aiResponse.getCompletionMessage());
                    cognitionChatRoom.incrementStep();
                    
                    // 채팅방 저장
                    return cognitionChatRoomRepository.save(cognitionChatRoom)
                            .map(savedRoom -> CognitionChatResponseDTO.builder()
                                    .sessionId(request.getSessionId())
                                    .completionMessage(aiResponse.getCompletionMessage())
                                    .build());
                });
    }

    // 오염강박 Step 4: 인지적 재구성 (process5 연결)
    private Mono<CognitionChatResponseDTO> processContaminationObsessionStep4(CognitionChatRequestDTO_1 request, CognitionChatRoom cognitionChatRoom) {
        
        // obsession_type 확인 (첫 번째 AI 응답에서 가져옴)
        String obsessionType = extractObsessionTypeFromHistory(cognitionChatRoom);
        
        // conversation_history를 DTO로 변환
        List<CognitionConversationDTO> conversationHistory = convertToConversationDTOs(cognitionChatRoom);
        
        // process_chat_5 연결
        return aiService.callProcessChat5(request.getSessionId(), obsessionType, conversationHistory)
                .flatMap(aiResponse -> {
                    // AI 응답을 conversation_history에 추가
                    java.util.Map<String, Object> assistantContent = java.util.Map.of(
                        "intro_message", aiResponse.getIntroMessage(),
                        "cognitive_restructuring", aiResponse.getCognitiveRestructuring(),
                        "encouragement_message", aiResponse.getEncouragementMessage(),
                        "insight_message", aiResponse.getInsightMessage(),
                        "final_encouragement", aiResponse.getFinalEncouragement(),
                        "choice_question", aiResponse.getChoiceQuestion()
                    );
                    
                    cognitionChatRoom.addConversation("assistant", assistantContent);
                    cognitionChatRoom.incrementStep();
                    
                    // 채팅방 저장
                    return cognitionChatRoomRepository.save(cognitionChatRoom)
                            .map(savedRoom -> CognitionChatResponseDTO.builder()
                                    .sessionId(request.getSessionId())
                                    .introMessage(aiResponse.getIntroMessage())
                                    .cognitiveRestructuring(aiResponse.getCognitiveRestructuring())
                                    .encouragementMessage(aiResponse.getEncouragementMessage())
                                    .insightMessage(aiResponse.getInsightMessage())
                                    .finalEncouragement(aiResponse.getFinalEncouragement())
                                    .choiceQuestion(aiResponse.getChoiceQuestion())
                                    .build());
                });
    }

    // conversation_history에서 obsession_type 추출
    private String extractObsessionTypeFromHistory(CognitionChatRoom cognitionChatRoom) {
        // 첫 번째 assistant 응답에서 obsession_type 추출
        for (CognitionConversation conversation : cognitionChatRoom.getConversationHistory()) {
            if ("assistant".equals(conversation.getRole()) && conversation.getContent() instanceof java.util.Map) {
                java.util.Map<String, Object> content = (java.util.Map<String, Object>) conversation.getContent();
                if (content.containsKey("obsession_type")) {
                    return (String) content.get("obsession_type");
                }
            }
        }
        return "기타강박"; // 기본값
    }

    // conversation_history를 DTO로 변환
    private List<CognitionConversationDTO> convertToConversationDTOs(CognitionChatRoom chatRoom) {
        List<CognitionConversationDTO> items = new ArrayList<>();
        for (CognitionConversation item : chatRoom.getConversationHistory()) {
            items.add(new CognitionConversationDTO(item.getRole(), item.getContent()));
        }
        return items;
    }
}
