package com.study.mindit.domain.prevention.application;

import com.study.mindit.domain.chat.domain.ObsessionConversation;
import com.study.mindit.domain.chat.domain.repository.ObsessionChatRoomRepository;
import com.study.mindit.domain.prevention.domain.PreventionReport;
import com.study.mindit.domain.prevention.domain.repository.PreventionReportRepository;
import com.study.mindit.domain.prevention.dto.request.*;
import com.study.mindit.domain.prevention.dto.response.PreviousAnxietyDTO;
import com.study.mindit.domain.prevention.dto.response.ReportResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Service
public class PreventionService {

    private final ObsessionChatRoomRepository chatRoomRepository;
    private final PreventionReportRepository preventionReportRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    /**
     * 1. 불안 위계표 조회 (chatRoomId로) - situation들만 배열로 반환
     */
    public Mono<List<String>> getAnxietyHierarchy(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom을 찾을 수 없습니다: " + chatRoomId)))
                .map(chatRoom -> {
                    // conversation_history에서 anxiety_scores 데이터 찾기
                    if (chatRoom.getConversationHistory() != null) {
                        for (ObsessionConversation conversation : chatRoom.getConversationHistory()) {
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
     * 2. 불안정도 입력 (이전/이번) - DB에 직접 저장
     */
    public Mono<String> inputAnxietyLevel(AnxietyInputRequestDTO request) {
        String sessionId = request.getSessionId();
        
        // 진행 중인 리포트 조회
        return preventionReportRepository.findOngoingReportBySessionId(sessionId)
                .switchIfEmpty(Mono.defer(() -> {
                    // 진행 중인 문서가 없으면 새로 생성
                    log.info("진행 중인 리포트가 없어서 새로 생성 - sessionId: {}", sessionId);
                    PreventionReport newReport = PreventionReport.builder()
                            .sessionId(sessionId)
                            .isCompleted(false)
                            .build();
                    return preventionReportRepository.save(newReport);
                }))
                .flatMap(existingReport -> {
                    // MongoDB Update로 직접 필드 업데이트
                    Query query = new Query(Criteria.where("_id").is(existingReport.getId()));
                    Update update = new Update();
                    
                    if (request.getAnxietyType() == com.study.mindit.domain.prevention.domain.AnxietyType.START) {
                        update.set("anxiety_level_start", request.getAnxietyLevel());
                    } else {
                        update.set("anxiety_level_end", request.getAnxietyLevel());
                    }
                    
                    return mongoTemplate.updateFirst(query, update, PreventionReport.class)
                            .thenReturn(existingReport);
                })
                .map(savedReport -> {
                    log.info("불안정도 저장 완료 - sessionId: {}, type: {}, level: {}", 
                            request.getSessionId(), request.getAnxietyType(), request.getAnxietyLevel());
                    
                    // anxietyType에 따른 응답 메시지
                    if (request.getAnxietyType() == com.study.mindit.domain.prevention.domain.AnxietyType.START) {
                        return "이전 불안 정도가 입력되었습니다";
                    } else {
                        return "이후 불안 정도가 입력되었습니다";
                    }
                });
    }

    /**
     * 3. 강박상황 입력 - DB에 직접 저장
     */
    public Mono<String> inputSituation(SituationInputRequestDTO request) {
        String sessionId = request.getSessionId();
        
        // 진행 중인 리포트 조회
        return preventionReportRepository.findOngoingReportBySessionId(sessionId)
                .switchIfEmpty(Mono.defer(() -> {
                    // 진행 중인 문서가 없으면 새로 생성
                    log.info("진행 중인 리포트가 없어서 새로 생성 - sessionId: {}", sessionId);
                    PreventionReport newReport = PreventionReport.builder()
                            .sessionId(sessionId)
                            .isCompleted(false)
                            .build();
                    return preventionReportRepository.save(newReport);
                }))
                .flatMap(existingReport -> {
                    // MongoDB Update로 직접 필드 업데이트
                    Query query = new Query(Criteria.where("_id").is(existingReport.getId()));
                    Update update = new Update().set("obsession_situation", request.getObsessiveSituation());
                    
                    return mongoTemplate.updateFirst(query, update, PreventionReport.class)
                            .thenReturn(existingReport);
                })
                .map(savedReport -> {
                    log.info("강박상황 저장 완료 - sessionId: {}, situation: {}", 
                            request.getSessionId(), request.getObsessiveSituation());
                    
                    return "강박상황이 저장되었습니다.";
                });
    }

    /**
     * 4. 강박사고 입력 - DB에 직접 저장
     */
    public Mono<String> inputThought(ThoughtInputRequestDTO request) {
        String sessionId = request.getSessionId();
        
        // 진행 중인 리포트 조회
        return preventionReportRepository.findOngoingReportBySessionId(sessionId)
                .switchIfEmpty(Mono.defer(() -> {
                    // 진행 중인 문서가 없으면 새로 생성
                    log.info("진행 중인 리포트가 없어서 새로 생성 - sessionId: {}", sessionId);
                    PreventionReport newReport = PreventionReport.builder()
                            .sessionId(sessionId)
                            .isCompleted(false)
                            .build();
                    return preventionReportRepository.save(newReport);
                }))
                .flatMap(existingReport -> {
                    // MongoDB Update로 직접 필드 업데이트
                    Query query = new Query(Criteria.where("_id").is(existingReport.getId()));
                    Update update = new Update().set("obsession_thought", request.getObsessiveThought());
                    
                    return mongoTemplate.updateFirst(query, update, PreventionReport.class)
                            .thenReturn(existingReport);
                })
                .map(savedReport -> {
                    log.info("강박사고 저장 완료 - sessionId: {}, thought: {}", 
                            request.getSessionId(), request.getObsessiveThought());
                    
                    return "강박사고가 저장되었습니다.";
                });
    }

    /**
     * 5. 반응방지 리포트 생성 (DB에서 조회 후 완료 처리)
     */
    public Mono<ReportResponseDTO> generateReport(ReportRequestDTO request) {
        String sessionId = request.getSessionId();
        
        // 진행 중인 리포트 조회
        return preventionReportRepository.findOngoingReportBySessionId(sessionId)
                .switchIfEmpty(Mono.error(new RuntimeException("저장된 데이터가 없습니다. 먼저 불안정도, 강박상황, 강박사고를 입력해주세요.")))
                .flatMap(existingReport -> {
                    // 필수 데이터 확인
                    if (existingReport.getAnxietyLevelStart() == null || 
                        existingReport.getAnxietyLevelEnd() == null ||
                        existingReport.getObsessionSituation() == null ||
                        existingReport.getObsessionThought() == null) {
                        return Mono.error(new RuntimeException("필수 데이터가 누락되었습니다. 모든 항목을 입력해주세요."));
                    }
                    
                    // 이번 주 반응방지 횟수 계산
                    return getThisWeekCount(sessionId)
                            .flatMap(thisWeekCount -> {
                                // 주차 계산
                                LocalDate today = LocalDate.now();
                                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                                int weekNumber = today.get(weekFields.weekOfYear());
                                
                                // MongoDB Update로 직접 필드 업데이트
                                Query query = new Query(Criteria.where("_id").is(existingReport.getId()));
                                Update update = new Update()
                                        .set("session_duration_seconds", request.getSessionDurationSeconds())
                                        .set("session_start_time", LocalDateTime.now().minusSeconds(request.getSessionDurationSeconds()))
                                        .set("session_end_time", LocalDateTime.now())
                                        .set("week_number", weekNumber)
                                        .set("session_number_in_week", thisWeekCount)
                                        .set("is_completed", true);
                                
                                return mongoTemplate.updateFirst(query, update, PreventionReport.class)
                                        .thenReturn(existingReport)
                                        .flatMap(savedReport -> getPreviousAnxietyData(sessionId, existingReport.getAnxietyLevelStart(), existingReport.getAnxietyLevelEnd())
                                                .flatMap(previousData -> {
                                                    // 반응방지 시간 포맷팅
                                                    int minutes = request.getSessionDurationSeconds() / 60;
                                                    int seconds = request.getSessionDurationSeconds() % 60;
                                                    String sessionDuration = String.format("%d분 %d초", minutes, seconds);
                                                    
                                                    // 최종 리포트 데이터 생성
                                                    ReportResponseDTO reportResponse = ReportResponseDTO.builder()
                                                            .obsessiveSituation(existingReport.getObsessionSituation())
                                                            .thisWeekCount(thisWeekCount)
                                                            .sessionDuration(sessionDuration)
                                                            .beforeAnxietyLevel(existingReport.getAnxietyLevelStart())
                                                            .afterAnxietyLevel(existingReport.getAnxietyLevelEnd())
                                                            .obsessiveThought(existingReport.getObsessionThought())
                                                            .previousAnxietyData(previousData)
                                                            .reportDate(formatKoreanDate(LocalDateTime.now()))
                                                            .reportTime(formatKoreanTime(LocalDateTime.now()))
                                                            .build();
                                                    
                                                    // 최종 리포트 데이터를 prevention_reports에 저장
                                                    PreventionReport finalReport = existingReport.toBuilder()
                                                            .sessionDurationSeconds(request.getSessionDurationSeconds())
                                                            .isCompleted(true)
                                                            .build();
                                                    
                                                    return preventionReportRepository.save(finalReport)
                                                            .thenReturn(reportResponse);
                                                }));
                            });
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
                    // createdAt이 null이면 현재 시간 사용
                    LocalDateTime reportDate = report.getCreatedAt() != null ? 
                            report.getCreatedAt() : LocalDateTime.now();
                    String formattedDate = formatDate(reportDate);
                    return PreviousAnxietyDTO.builder()
                            .beforeAnxietyLevel(report.getAnxietyLevelStart())
                            .afterAnxietyLevel(report.getAnxietyLevelEnd())
                            .sessionDate(formattedDate)
                            .build();
                })
                .filter(dto -> dto.getBeforeAnxietyLevel() != null && dto.getAfterAnxietyLevel() != null)
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
        if (dateTime == null) {
            return "알 수 없음";
        }
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        return String.format("%d월 %d일", month, day);
    }

    /**
     * 날짜를 "10월 31일 금요일" 형태로 포맷팅
     */
    private String formatKoreanDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "알 수 없음";
        }
        
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        String dayOfWeek = dateTime.getDayOfWeek().getDisplayName(
            java.time.format.TextStyle.FULL, 
            Locale.KOREAN
        );
        
        return String.format("%d월 %d일 %s", month, day, dayOfWeek);
    }

    /**
     * 시간을 한국 시간 "오전 9시 36분" 형태로 포맷팅
     */
    private String formatKoreanTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "알 수 없음";
        }
        
        // 한국 시간으로 변환
        ZonedDateTime koreanTime = dateTime.atZone(ZoneId.of("Asia/Seoul"));
        
        int hour = koreanTime.getHour();
        int minute = koreanTime.getMinute();
        
        String amPm = hour < 12 ? "오전" : "오후";
        int displayHour = hour == 0 ? 12 : (hour > 12 ? hour - 12 : hour);
        
        return String.format("%s %d시 %d분", amPm, displayHour, minute);
    }
}
