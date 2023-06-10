package com.example.ussd.integrations;

import com.example.ussd.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
public class MessageService {
    private final RestTemplate restTemplate;

    @Value("${africaTalking.smsUrl}")
    private String smsUrl;

    @Value("${africaTalking.apiKey}")
    private String apiKey;

    @Value("${africaTalking.username}")
    private String username;

    public MessageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public void send(String phoneNumber) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.set("apiKey", apiKey);

            String body = "username=sandbox&to=" + StringUtils.deleteWhitespace(phoneNumber) + "&message=Registered%20user";

            log.info("Message params :{}", body);
            RequestEntity<String> requestEntity = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(smsUrl));
            var response = restTemplate.exchange(requestEntity, String.class);
            log.info("Message response :{}", response);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new CustomException("Failed to send SMS.");
            }
        } catch (Exception e) {
            log.error("Error occurred while sending SMS: {}", e.getMessage());
            throw e;
        }
    }
}