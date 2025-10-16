package com.study.mindit.domain.prevention.application;

import com.study.mindit.domain.chat.domain.repository.OBChatRoomRepository;
import com.study.mindit.domain.prevention.domain.PreventionReport;
import com.study.mindit.domain.prevention.domain.repository.PreventionReportRepository;
import com.study.mindit.domain.prevention.dto.request.*;
import com.study.mindit.domain.prevention.dto.response.AnxietyHierarchyResponseDTO;
import com.study.mindit.domain.prevention.dto.response.PreviousAnxietyDTO;
import com.study.mindit.domain.prevention.dto.response.ReportResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PreventionService {

    private final OBChatRoomRepository chatRoomRepository;
    private final PreventionReportRepository preventionReportRepository;
    
    // 임시 저장소 (세션 진행 중 데이터 임시 보관)
    private final Map<String, Map<String, Object>> tempStorage = new HashMap<>();

    /**
     * 1. 불안 위계표 조회 (chatRoomId로) - situation들만 배열로 반환
     */
    public Mono<List<String>> getAnxietyHierarchy(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom을 찾을 수 없습니다: " + chatRoomId)))
                .map(chatRoom -> {
                    // conversation_history에서 anxiety_scores 데이터 찾기
                    if (chatRoom.getConversationHistory() != null) {
                        for (com.study.mindit.domain.chat.domain.OBConversation conversation : chatRoom.getConversationHistory()) {
                            Object content = conversation.getContent();
                            
                            if (content instanceof java.util.Map) {
                                java.util.Map<String, Object> contentMap = (java.util.Map<String, Object>) content;
                                Object typeValue = contentMap.get("type");
                                
                                if ("anxiety_scores".equals(typeValue)) {
                                    java.util.Map<String, Object> data = (java.util.Map<String, Object>) contentMap.get("data");
                                    if (data != null) {
                                        java.util.List<java.util.Map<String, Object>> situations = 
                                            (java.util.List<java.util.Map<String, Object>>) data.get("situations");
                                        
                                        if (situations != null && !situations.isEmpty()) {
                                            // 점수순으로 정렬 (낮은 점수 -> 높은 점수)
                                            situations.sort((a, b) -> {
                                                Integer scoreA = (Integer) a.get("score");
                                                Integer scoreB = (Integer) b.get("score");
                                                return scoreA.compareTo(scoreB);
                                            });
                                            
                                            // situation 텍스트들만 추출해서 리스트로 반환
                                            return situations.stream()
                                                    .map(situation -> (String) situation.get("situation"))
                                                    .collect(java.util.stream.Collectors.toList());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return new java.util.ArrayList<String>();
                });
    }

    /**
     * 2. 불안정도 입력 (이전/이번)
     */
    public Mono<String> inputAnxietyLevel(AnxietyInputRequestDTO request) {
        String key = request.getSessionId();
        
        // 임시 저장소에 불안정도 저장
        tempStorage.computeIfAbsent(key, k -> new HashMap<>())
                .put(request.getAnxietyType().name().toLowerCase() + "_anxiety_level", request.getAnxietyLevel());
        
        log.info("불안정도 저장 완료 - sessionId: {}, type: {}, level: {}", 
                request.getSessionId(), request.getAnxietyType(), request.getAnxietyLevel());
        
        // anxietyType에 따른 응답 메시지
        String responseMessage;
        if (request.getAnxietyType() == com.study.mindit.domain.prevention.domain.AnxietyType.BEFORE) {
            responseMessage = "이전 불안 정도가 입력되었습니다";
        } else {
            responseMessage = "이후 불안 정도가 입력되었습니다";
        }
        
        return Mono.just(responseMessage);
    }

    /**
     * 3. 강박상황 입력
     */
    public Mono<String> inputSituation(SituationInputRequestDTO request) {
        String key = request.getSessionId();
        
        // 임시 저장소에 강박상황 저장
        tempStorage.computeIfAbsent(key, k -> new HashMap<>())
                .put("obsessive_situation", request.getObsessiveSituation());
        
        log.info("강박상황 저장 완료 - sessionId: {}, situation: {}", 
                request.getSessionId(), request.getObsessiveSituation());
        
        return Mono.just("강박상황이 저장되었습니다.");
    }

    /**
     * 4. 강박사고 입력
     */
    public Mono<String> inputThought(ThoughtInputRequestDTO request) {
        String key = request.getSessionId();
        
        // 임시 저장소에 강박사고 저장
        tempStorage.computeIfAbsent(key, k -> new HashMap<>())
                .put("obsessive_thought", request.getObsessiveThought());
        
        log.info("강박사고 저장 완료 - sessionId: {}, thought: {}", 
                request.getSessionId(), request.getObsessiveThought());
        
        return Mono.just("강박사고가 저장되었습니다.");
    }

    /**
     * 5. 반응방지 리포트 생성 (MongoDB에 저장)
     */
    public Mono<ReportResponseDTO> generateReport(ReportRequestDTO request) {
        String key = request.getSessionId();
        Map<String, Object> data = tempStorage.get(key);
        
        if (data == null) {
            return Mono.error(new RuntimeException("저장된 데이터가 없습니다. 먼저 불안정도, 강박상황, 강박사고를 입력해주세요."));
        }
        
        // 데이터 추출
        Integer beforeAnxiety = (Integer) data.get("before_anxiety_level");
        Integer afterAnxiety = (Integer) data.get("after_anxiety_level");
        String obsessiveSituation = (String) data.get("obsessive_situation");
        String obsessiveThought = (String) data.get("obsessive_thought");
        
        // 이번 주 반응방지 횟수 계산
        return getThisWeekCount(key)
                .flatMap(thisWeekCount -> {
                    // 주차 계산
                    LocalDate today = LocalDate.now();
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int weekNumber = today.get(weekFields.weekOfYear());
                    
                    // PreventionReport 생성 및 저장
                    PreventionReport report = PreventionReport.builder()
                            .sessionId(key)
                            .obsessionSituation(obsessiveSituation)
                            .obsessionThought(obsessiveThought)
                            .anxietyLevelStart(beforeAnxiety)
                            .anxietyLevelEnd(afterAnxiety)
                            .sessionDurationSeconds(request.getSessionDurationSeconds())
                            .sessionStartTime(LocalDateTime.now().minusSeconds(request.getSessionDurationSeconds()))
                            .sessionEndTime(LocalDateTime.now())
                            .weekNumber(weekNumber)
                            .sessionNumberInWeek(thisWeekCount)
                            .isCompleted(true)
                            .build();
                    
                    return preventionReportRepository.save(report)
                            .flatMap(savedReport -> getPreviousAnxietyData(key, beforeAnxiety, afterAnxiety)
                                    .map(previousData -> {
                                        // 반응방지 시간 포맷팅
                                        int minutes = request.getSessionDurationSeconds() / 60;
                                        int seconds = request.getSessionDurationSeconds() % 60;
                                        String sessionDuration = String.format("%d분 %d초", minutes, seconds);
                                        
                                        return ReportResponseDTO.builder()
                                                .obsessiveSituation(obsessiveSituation)
                                                .thisWeekCount(thisWeekCount)
                                                .sessionDuration(sessionDuration)
                                                .beforeAnxietyLevel(beforeAnxiety)
                                                .afterAnxietyLevel(afterAnxiety)
                                                .obsessiveThought(obsessiveThought)
                                                .previousAnxietyData(previousData)
                                                .reportTime(LocalDateTime.now())
                                                .build();
                                    }));
                });
    }

    /**
     * 이번 주 반응방지 횟수 계산
     */
    private Mono<Integer> getThisWeekCount(String sessionId) {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = today.get(weekFields.weekOfYear());
        
        return preventionReportRepository.countBySessionIdAndWeekNumber(sessionId, weekNumber)
                .map(count -> count.intValue() + 1); // 현재 세션 포함
    }

    /**
     * 현재 세션을 포함한 최근 세션들의 불안정도 데이터 조회 (총 최대 3개, 현재 세션이 마지막)
     */
    private Mono<List<PreviousAnxietyDTO>> getPreviousAnxietyData(String sessionId, Integer currentBeforeAnxiety, Integer currentAfterAnxiety) {
        // 이전 세션들 조회 (최대 2개)
        return preventionReportRepository.findBySessionIdOrderByCreatedAtDesc(sessionId)
                .filter(report -> report.getIsCompleted() && 
                        report.getAnxietyLevelStart() != null && 
                        report.getAnxietyLevelEnd() != null)
                .take(2) // 이전 세션 최대 2개만
                .map(report -> {
                    String formattedDate = formatDate(report.getCreatedAt());
                    return PreviousAnxietyDTO.builder()
                            .beforeAnxietyLevel(report.getAnxietyLevelStart())
                            .afterAnxietyLevel(report.getAnxietyLevelEnd())
                            .sessionDate(formattedDate)
                            .build();
                })
                .collectList()
                .map(previousData -> {
                    List<PreviousAnxietyDTO> anxietyDataList = new ArrayList<>();
                    
                    // 이전 세션들을 먼저 추가
                    anxietyDataList.addAll(previousData);
                    
                    // 현재 세션을 마지막에 추가
                    String currentFormattedDate = formatDate(LocalDateTime.now());
                    anxietyDataList.add(PreviousAnxietyDTO.builder()
                            .beforeAnxietyLevel(currentBeforeAnxiety)
                            .afterAnxietyLevel(currentAfterAnxiety)
                            .sessionDate(currentFormattedDate)
                            .build());
                    
                    return anxietyDataList;
                });
    }

    /**
     * 날짜를 "몇월 몇일" 형태로 포맷팅
     */
    private String formatDate(LocalDateTime dateTime) {
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        return String.format("%d월 %d일", month, day);
    }
}
