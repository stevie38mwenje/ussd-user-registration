package com.example.ussd.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class RestTemplateConfig {
    public static final long CONNECTION_TIMEOUT_DURATION = 5;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_DURATION))
                .setReadTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_DURATION))
                .messageConverters(getMessageConverters())
                .build();
    }
    private List<HttpMessageConverter<?>> getMessageConverters() {
        FormHttpMessageConverter formConverter = new FormHttpMessageConverter();
        formConverter.setCharset(StandardCharsets.UTF_8);
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        return Arrays.asList(formConverter, stringConverter);
    }
}
