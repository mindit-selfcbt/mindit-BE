package com.study.mindit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class MinditApplication {
    public static void main(String[] args) {
        SpringApplication.run(MinditApplication.class, args);
    }
}
