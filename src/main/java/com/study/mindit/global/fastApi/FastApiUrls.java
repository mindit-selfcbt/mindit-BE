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
}
