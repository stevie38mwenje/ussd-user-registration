package com.example.ussd.controller;

import com.example.ussd.domain.dto.UssdRequest;
import com.example.ussd.service.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UssdControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserRegistrationService userRegistrationService;

    @InjectMocks
    private UssdController ussdController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ussdController).build();
    }

    @Test
    public void testHandleUssdRequest() throws Exception {
        // Mock UssdRequest
        UssdRequest ussdRequest = new UssdRequest();
        ussdRequest.setText("1");

        // Mock expected response
        String expectedResponse = "CON Enter your Name:";

        // Mock the behavior of the userRegistrationService
        when(userRegistrationService.performUssdRequest(ussdRequest)).thenReturn(expectedResponse);

        // Build the request
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/v1/api/user/ussd")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("text", ussdRequest.getText());

        // Perform the request and assert the response
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals(expectedResponse, response);

        // Verify that the userRegistrationService was called with the correct UssdRequest
        verify(userRegistrationService, times(1)).performUssdRequest(ussdRequest);
    }

}