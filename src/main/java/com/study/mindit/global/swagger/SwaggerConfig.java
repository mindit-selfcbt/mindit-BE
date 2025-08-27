package com.study.mindit.global.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mindit Springdoc")
                        .description("Springdoc을 사용한 Mindit Swagger UI")
                        .version("1.0.0"));
    }
}
