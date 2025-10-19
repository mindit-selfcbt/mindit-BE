package com.study.mindit.global.fastApi;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class FastApiUrls {
    @Value("${fastApi.urls.obsession.analyze_chat_1}")
    private String analyze1;

    @Value("${fastApi.urls.obsession.analyze_chat_2}")
    private String analyze2;

    @Value("${fastApi.urls.obsession.analyze_chat_3}")
    private String analyze3;

    @Value("${fastApi.urls.obsession.analyze_chat_4}")
    private String analyze4;

    @Value("${fastApi.urls.obsession.analyze_chat_5}")
    private String analyze5;

    @Value("${fastApi.urls.obsession.analyze_chat_6}")
    private String analyze6;

    @Value("${fastApi.urls.obsession.analyze_chat_7}")
    private String analyze7;

    @Value("${fastApi.urls.obsession.analyze_chat_8}")
    private String analyze8;

    @Value("${fastApi.urls.obsession.analyze_chat_9}")
    private String analyze9;

    @Value("${fastApi.urls.obsession.analyze_report}")
    private String analyzeReport;

    @Value("${fastApi.urls.obsession.analyze_report_2}")
    private String analyzeReport2;

    @Value("${fastApi.urls.cognition.process_chat_1}")
    private String process1;

    @Value("${fastApi.urls.cognition.process_chat_2}")
    private String process2;

    @Value("${fastApi.urls.cognition.process_chat_3}")
    private String process3;

    @Value("${fastApi.urls.cognition.process_chat_4}")
    private String process4;

    @Value("${fastApi.urls.cognition.process_chat_5}")
    private String process5;

    @Value("${fastApi.urls.cognition.process_chat_6}")
    private String process6;

    @Value("${fastApi.urls.cognition.process_chat_7}")
    private String process7;

    @Value("${fastApi.urls.obsession.process_report}")
    private String processReport;

    // Cognition process_chat 메서드들
    public String getProcessChat1() {
        return process1;
    }

    public String getProcessChat2() {
        return process2;
    }

    public String getProcessChat3() {
        return process3;
    }

    public String getProcessChat4() {
        return process4;
    }

    public String getProcessChat5() {
        return process5;
    }

    public String getProcessChat6() {
        return process6;
    }

    public String getProcessChat7() {
        return process7;
    }

    // Obsession analyze 메서드들
    public String getAnalyze1() {
        return analyze1;
    }

    public String getAnalyze2() {
        return analyze2;
    }

    public String getAnalyze3() {
        return analyze3;
    }

    public String getAnalyze4() {
        return analyze4;
    }

    public String getAnalyze5() {
        return analyze5;
    }

    public String getAnalyze6() {
        return analyze6;
    }

    public String getAnalyze7() {
        return analyze7;
    }

    public String getAnalyze8() {
        return analyze8;
    }

    public String getAnalyze9() {
        return analyze9;
    }

    public String getProcessReport() {
        return processReport;
    }

    public String getAnalyzeReport() {
        return analyzeReport;
    }

    public String getAnalyzeReport2() {
        return analyzeReport2;
    }
}
