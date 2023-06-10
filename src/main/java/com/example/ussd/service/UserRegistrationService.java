package com.example.ussd.service;

import com.example.ussd.domain.UserDetails;
import com.example.ussd.domain.dto.UssdRequest;

public interface UserRegistrationService {

    String performUssdRequest(UssdRequest request) throws Exception;
}
