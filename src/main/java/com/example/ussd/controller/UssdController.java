package com.example.ussd.controller;

import com.example.ussd.domain.UserDetails;
import com.example.ussd.domain.dto.GenericResponse;
import com.example.ussd.domain.dto.UssdRequest;
import com.example.ussd.integrations.MessageService;
import com.example.ussd.repository.UserDetailsRepository;
import com.example.ussd.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("v1/api/user")
@RequiredArgsConstructor
@Slf4j
public class UssdController {
    private final UserRegistrationService userRegistrationService;

    @PostMapping(value = "/ussd", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String handleUssdRequest(@ModelAttribute UssdRequest request) throws Exception {
        log.info("ussd request: {}", request);
        return userRegistrationService.performUssdRequest(request);
    }

}
