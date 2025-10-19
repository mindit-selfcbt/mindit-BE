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
    private String processChat1;

    @Value("${fastApi.urls.cognition.process_chat_2}")
    private String processChat2;

    @Value("${fastApi.urls.cognition.process_chat_3}")
    private String processChat3;

    @Value("${fastApi.urls.cognition.process_chat_4}")
    private String processChat4;

    @Value("${fastApi.urls.cognition.process_chat_5}")
    private String processChat5;

    @Value("${fastApi.urls.cognition.process_chat_6}")
    private String processChat6;

    @Value("${fastApi.urls.cognition.process_chat_7}")
    private String processChat7;

}
