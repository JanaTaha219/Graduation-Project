package com.example.zh2t.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class OpenApiConfig {
   // @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("zh2t-public")
                .pathsToMatch("/**")
                .build();
    }
}