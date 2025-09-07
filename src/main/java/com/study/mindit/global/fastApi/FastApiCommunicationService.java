package com.study.mindit.global.fastApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastApiCommunicationService {
    private final RestTemplate restTemplate;
    private final FastApiUrls fastApiUrls;
}
