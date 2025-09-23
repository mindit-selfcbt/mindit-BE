package com.study.mindit.global.fastApi;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class FastApiUrls {
    @Value("${fastApi.urls.analyze_chat_1}")
    private String analyze1;

    @Value("${fastApi.urls.analyze_chat_2}")
    private String analyze2;

    @Value("${fastApi.urls.analyze_chat_3}")
    private String analyze3;
}
