package com.example.ussd.service;

import com.example.ussd.domain.UserDetails;
import com.example.ussd.domain.dto.GenericResponse;
import com.example.ussd.domain.dto.UserDetailsResponse;
import com.example.ussd.domain.dto.UssdRequest;
import com.example.ussd.exception.CustomException;
import com.example.ussd.integrations.MessageService;
import com.example.ussd.repository.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.ussd.Constants.REGISTER_USER_MENU;
import static com.example.ussd.Constants.RETRIEVE_USER_DETAIL_MENU;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationServiceImpl implements UserRegistrationService{
    private final UserDetailsRepository userDetailsRepository;
    private final MessageService messageService;
    public String registerUser(String phoneNumber, String name, String age) {
        var phone = StringUtils.deleteWhitespace(phoneNumber);
        var user = UserDetails.builder().age(Integer.valueOf(age))
                .phoneNumber(phone)
                .name(name)
                .build();
        log.info("User creation request : {}",user);
        var newUser = userDetailsRepository.save(user);
        log.info("User creation response : {}",newUser);
        try {
            messageService.send(phone);
        } catch (Exception e) {
            throw new CustomException("Failed to send message. User registration rolled back.");
        }
        return newUser.toString();
    }

    public UserDetails fetchUser(String phoneNumber) {
        var user = userDetailsRepository.findByPhoneNumber(StringUtils.deleteWhitespace(phoneNumber));
        log.info("fetched user: {}",user);
        if (user == null) {
            throw new CustomException("User not found for phone number: " + phoneNumber);
        }
        return user;
    }

    @Override
    public String performUssdRequest(UssdRequest request) throws Exception {
        String text = request.getText();

        StringBuilder response = new StringBuilder("");

        if (text.isEmpty()) {
            response.append("CON What would you like to do?\n1. Register User\n2. Check Account\n");
        } else if (text.equals("1")) {
            response.append(REGISTER_USER_MENU);
        } else if (text.startsWith("1*")) {
            response.append(processRegistrationInput(text));
        } else if (text.equals("2")) {
            response.append(RETRIEVE_USER_DETAIL_MENU);
        } else if (text.startsWith("2*")) {
            response.append(processRetrieveDetailsInput(text));
        } else {
            response.append("CON Invalid input. Please try again\n");
            response.append("1. Register a user\n");
            response.append("2. Retrieve registered details");
        }
        return response.toString();
    }


    public String processRegistrationInput(String text) throws Exception {
        String[] parts = text.split("\\*");
        int partCount = parts.length;
        if (partCount == 2) {
            return "CON Enter your Name:";
        } else if (partCount == 3) {
            return "CON Enter your Age:";
        } else if (partCount == 4) {
            String enteredPhoneNumber = parts[1];
            String name = parts[2];
            String age = parts[3];
            //validate entered phone number
            if (userDetailsRepository.existsByPhoneNumber(enteredPhoneNumber)) {
                return "END Phone number already registered.";
            }
            if (enteredPhoneNumber.length()<7 && enteredPhoneNumber.length()>15) {
                return "Phone number is invalid.";
            }
            var userResponse = registerUser(enteredPhoneNumber, name, age);

            if (userResponse != null) {
                messageService.send(enteredPhoneNumber);
                return "END Registration successful! You will receive a confirmation SMS.";
            } else {
                return "END Registration failed. Please try again.";
            }
        }

        return "";
    }


    private String processRetrieveDetailsInput(String text) {
        String[] phoneDetails = text.split("\\*");
        //check if valid phone number was provided and extract it from phoneDetails
        if (phoneDetails.length == 2) {
            String requestedPhoneNumber = phoneDetails[1];
            UserDetails userDetails = fetchUser(requestedPhoneNumber);

            if (userDetails != null) {
                StringBuilder response = new StringBuilder("END User Details:\n");
                response.append("Name: ").append(userDetails.getName()).append("\n");
                response.append("Phone Number: ").append(userDetails.getPhoneNumber()).append("\n");
                response.append("Age: ").append(userDetails.getAge()).append("\n");
                return response.toString();
            } else {
                return "END User details not found for the requested phone number.";
            }
        } else {
            return "CON Invalid input. Enter the phone number to fetch its details:";
        }
    }
    }

